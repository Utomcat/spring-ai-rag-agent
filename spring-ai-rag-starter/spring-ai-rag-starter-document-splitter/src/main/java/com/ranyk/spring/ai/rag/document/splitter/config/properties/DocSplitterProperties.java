package com.ranyk.spring.ai.rag.document.splitter.config.properties;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLASS_NAME: DocSplitterProperties.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文档拆分器配置属性类
 * @date: 2026-07-01
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = DocSplitterProperties.CONFIG_PREFIX)
public class DocSplitterProperties {
    /**
     * 自定义 - 文档拆分器配置属性前缀
     */
    public static final String CONFIG_PREFIX = "doc.splitter";

    /**
     *  单个文本块的最大 token 数, 当达到该上限时尝试在合适的标点处断开, 若无法断开则直接截断. 默认为 800
     */
    private Integer chunkSize;
    /**
     * 最大可生成的块数量. 防止对超长文本无限分割, 达到此上限后即使还有剩余文本也停止生产新块(即直接截断,后续内容直接丢弃), 默认为 10000;
     */
    private Integer maxNumChunks;
    /**
     * 指定用于 Token 化的编码类型, 不同的编码对应不同的分词方式, 影响 Token 计数和分割结果, 默认为 CL100K_BASE (也是 OpenAI 的编码方式), 参考 {@link EncodingType} 枚举类
     */
    private String encodingType;
    /**
     * 最小字符数阈值, 用于判断是否在标点处分割. 当从块末尾查找标点且标点位置索引大于该值时, 才在该标点处分割, 否则继续按 Token 数量直接分割. 默认为 350
     */
    private Integer minChunkSizeCharts;
    /**
     * 最终块的最小字符长度, 如果切分出的块文本长度（trim 后）小于该值, 则丢弃该块, 不纳入结果列表. 默认为 5
     */
    private Integer minChunkLengthToEmbed;
    /**
     * 是否保留分隔符（主要是换行符 \n）。为 true 时保留并 trim 块文本；为 false 时将块中的换行符替换为空格再 trim; 默认为 true
     */
    private Boolean keepSeparator;
    /**
     * 定义 断句标点符号. 当块接近 chunkSize 时, 会从后向前查找这些符号的位置, 并尽量在最后一个符号处分割, 使块更自然. 默认为 [. ? ! \n]
     */
    private List<Character> punctuationMarks;
}
