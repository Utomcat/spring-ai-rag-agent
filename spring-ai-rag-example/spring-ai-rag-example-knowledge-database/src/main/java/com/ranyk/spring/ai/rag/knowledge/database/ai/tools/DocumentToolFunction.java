package com.ranyk.spring.ai.rag.knowledge.database.ai.tools;

import cn.hutool.json.JSONUtil;
import com.ranyk.spring.ai.rag.common.constant.FileTypeEnum;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto.DocumentDTO;
import com.ranyk.spring.ai.rag.knowledge.database.service.document.DocumentService;
import com.ranyk.spring.ai.rag.tool.facade.BaseTool;
import com.ranyk.spring.ai.rag.tool.registry.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: DocumentToolFunction.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 业务相关的 文档 AI 工具类
 * @date: 2026-07-13
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class DocumentToolFunction implements BaseTool {

    /**
     * 文档业务逻辑处理类对象
     */
    private final DocumentService documentService;

    /**
     * 构造方法 由 Spring IOC 容器创建当前 Bean 实例对象时自动调用注入相关 Bean 的依赖实例对象
     *
     * @param documentService 文档业务逻辑处理类对象
     */
    @Autowired
    public DocumentToolFunction(DocumentService documentService, ToolRegistry toolRegistry) {
        this.documentService = documentService;
        toolRegistry.register(getName(), this);
    }

    /**
     * 获取工具名称
     *
     * @return 工具名称 - 返回对应的实现类 Bean 名称
     */
    @Override
    public String getName() {
        return "documentToolFunction";
    }

    /**
     * 获取工具描述
     *
     * @return 工具描述 - 返回对应的实现类的描述信息
     */
    @Override
    public String getDescription() {
        return "- 知识库文档列表查询工具, 按需使用, 用于查询知识库中已上传的文档文件名列表";
    }

    /**
     * Spring AI Function Callback 工具类 - 依据知识库文档的分类类别, 查询指定列表的知识库文档的文件名列表
     *
     * @param categoryIds 需要查询的知识库文档分类ID列表
     * @return 所有文档文件名的列表字符串,通过构建成 AI 的模型调用
     */
    @Tool(description = "查询知识库中已上传的文件列表. 当用户询问 '知识库中有哪些文件'、'列出所有文档'、'当前知识库中有什么文件' 等问题时使用此工具, 分页查询, 默认传入 page 为 1, size 为 10, 下一页为之前传入的 page 值加一")
    public String getAllDocumentsFileName(
            @ToolParam(description = "文档分类ID, 可选参数, 不传或传 0 则查询所有分类的文件", required = false)
            List<Long> categoryIds,
            @ToolParam(description = "文档类型, 可选参数, 当传入 txt 时 该值转换为 .txt, 当传入 pdf 时 该值转换为 .pdf 等依次类推, 不传或传为空则查询所有类型的文件", required = false)
            List<String> fileTypes,
            @ToolParam(description = "页码, 可选参数, 默认为 1", required = false)
            Integer page,
            @ToolParam(description = "每页大小, 可选参数, 默认为 10", required = false)
            Integer size
    ) {
        log.info("调用 Spring AI Function Callback 工具类 - getAllDocumentsFileName , 其入参为: page => {} , size => {} , categoryIds => {} , fileTypes => {} ", page, size, JSONUtil.toJsonStr(categoryIds), JSONUtil.toJsonStr(fileTypes));
        log.info("对文件类型进行数据格式转换, 如 入参为 txt 则转换为 .txt, 入参为 pdf 则转换为 .pdf, 入参为 doc 则转换为 .doc, 入参为 docx 则转换为 .docx 依次进行转换");
        // 遍历 fileTypes , 对 fileTypes 进行数据过滤, 依据 FileTypeEnum 进行匹配, 并判定每个文件类型是否是以 . 开头, 如果不是则在前面添加 .
        if (Objects.nonNull(fileTypes) && !fileTypes.isEmpty()) {
            List<String> allValidSuffixes = FileTypeEnum.getAllSuffixes();
            fileTypes = fileTypes.stream()
                    .filter(Objects::nonNull)
                    .map(item -> item.startsWith(".") ? item.substring(1) : item)
                    .filter(suffix -> !suffix.isBlank())
                    .filter(suffix -> allValidSuffixes.stream()
                            .anyMatch(valid -> valid.equalsIgnoreCase(suffix)))
                    .distinct()
                    .map(suffix -> "." + suffix.toLowerCase())
                    .toList();
            log.info("过滤后的有效文件类型: {}", JSONUtil.toJsonStr(fileTypes));
        }
        DocumentDTO documentDTO = documentService.list(DocumentDTO.builder().categoryIds(categoryIds).fileTypes(fileTypes).page(page).size(size).build());
        if (Objects.isNull(documentDTO.getDataList()) || documentDTO.getDataList().isEmpty()) {
            return "当前知识库中没有文件";
        }
        StringBuilder builder = new StringBuilder("当前知识库中,一共有 " + documentDTO.getDataList().size() + " 个文件，文件列表如下：\n");
        documentDTO.getDataList().forEach(item -> builder.append("- ").append(item.getFileName()).append("\n"));
        log.info("当前最终 LLM 调用 Spring AI Function Callback 工具类 - getAllDocumentsFileName , 其返回结果为: {}", builder);
        return builder.toString();
    }
}
