package com.ranyk.spring.ai.rag.knowledge.database.domain.document.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto.DocumentDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.po.DocumentQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.vo.DocumentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * CLASS_NAME: DocumentMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据转换 MapStruct 接口
 * @date: 2026-06-28
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 转换为 知识库文档数据访问 VO 对象 {@link DocumentVO}
     *
     * @param documentDTO 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     * @return 知识库文档数据访问 VO 对象 {@link DocumentVO}
     */
    DocumentVO documentDTOToDocumentVO(DocumentDTO documentDTO);

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表转换为 知识库文档数据访问 VO 对象 {@link DocumentVO} 列表
     *
     * @param documentDTOs 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     * @return 知识库文档数据访问 VO 对象 {@link DocumentVO} 列表
     */
    List<DocumentVO> documentDTOListToDocumentVOList(List<DocumentDTO> documentDTOs);

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 转换为 知识库文档数据访问 Entity 对象 {@link Document}
     *
     * @param documentDTO 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     * @return 知识库文档数据访问 Entity 对象 {@link Document}
     */
    Document documentDTOToDocument(DocumentDTO documentDTO);

    /**
     * 将 知识库文档数据查询 PO 对象 {@link DocumentQueryPO} 转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     *
     * @param documentQueryPO 知识库文档数据查询 PO 对象 {@link DocumentQueryPO}
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     */
    @Mappings({
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "absolutePath", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "fileName", ignore = true),
            @Mapping(target = "filePath", ignore = true),
            @Mapping(target = "fileSize", ignore = true),
            @Mapping(target = "fileType", ignore = true),
            @Mapping(target = "title", ignore = true),
            @Mapping(target = "uploadUserId", ignore = true),
            @Mapping(target = "vectorCount", ignore = true),
    })
    DocumentDTO documentQueryPOToDocumentDTO(DocumentQueryPO documentQueryPO);

    /**
     * 将 知识库文档数据访问 Entity 对象 {@link Document} 转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     *
     * @param document 知识库文档数据访问 Entity 对象 {@link Document}
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     */
    @Mappings({
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true),
            @Mapping(target = "categoryIds", ignore = true),
            @Mapping(target = "fileTypes", ignore = true)
    })
    DocumentDTO documentToDocumentDTO(Document document);

    /**
     * 将 知识库文档数据访问 Entity 对象 {@link Document} 列表转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     *
     * @param documentList 知识库文档数据访问 Entity 对象 {@link Document} 列表
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     */
    List<DocumentDTO> documentListToDocumentDTOList(List<Document> documentList);

}
