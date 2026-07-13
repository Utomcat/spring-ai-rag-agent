package com.ranyk.spring.ai.rag.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * CLASS_NAME: MathUtils.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数学工具类
 * @date: 2026-06-24
 */
@Slf4j
@SuppressWarnings("unused")
public class MathUtils {

    /**
     * 计算两个向量的欧氏距离, 当两个向量维度不一致时, 返回 -1.0D
     *
     * @param a 向量1 float 数组
     * @param b 向量2 float 数组
     * @return 欧氏距离, Double 类型, 当两个向量维度不一致时, 返回 -1.0D
     */
    public static Double calculateEuclideanDistance(float[] a, float[] b) {
        if (a.length != b.length) {
            log.error("向量维度不一致, 无法计算欧氏距离, 直接返回 -1.0D");
            return -1.0D;
        }
        double sumSq = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sumSq += d * d;
        }
        return Math.sqrt(sumSq);
    }

    /**
     * 计算两个向量的余弦距离, 当两个向量维度不一致时, 返回 -1.0D
     *
     * @param a 向量1 float 数组
     * @param b 向量2 float 数组
     * @return 余弦距离, Double 类型, 范围 [0, 2], 值越小表示越相似; 当两个向量维度不一致时, 返回 -1.0D
     */
    public static Double calculateCosineDistance(float[] a, float[] b) {
        if (a.length != b.length) {
            log.error("向量维度不一致, 无法计算余弦距离, 直接返回 -1.0D");
            return -1.0D;
        }

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0 || normB == 0) {
            log.warn("向量模长为0, 无法计算余弦相似度, 直接返回 -1.0D");
            return -1.0D;
        }

        double cosineSimilarity = dotProduct / (normA * normB);
        cosineSimilarity = Math.clamp(cosineSimilarity, -1.0, 1.0);

        return 1.0 - cosineSimilarity;
    }

    /**
     * 计算两个向量之间的距离 使用 其他方式, 当两个向量维度不一致时, 返回 -1.0D
     *
     * @param a 向量1 float 数组
     * @param b 向量2 float 数组
     * @return 距离, Double 类型, 当两个向量维度不一致时, 返回 -1.0D
     */
    public static Double calculateOtherDistance(float[] a, float[] b) {
        if (a.length != b.length) {
            log.error("向量维度不一致, 无法计算距离, 直接返回 -1.0D");
            return -1.0D;
        }
        // TODO: 实现其他距离计算方式
        log.info("Using other distance calculation method, Please wait for implementation");
        return -1.0D;
    }

    /**
     * 计算两个向量之间的距离, 根据传入的类型调用对应的计算距离的方法, 当两个向量维度不一致时, 返回 -1.0D
     *
     * @param a            向量1 float 数组
     * @param b            向量2 float 数组
     * @param distanceType 距离类型, 参见 {@link DistanceTypeEnum}
     * @return 距离, Double 类型, 当两个向量维度不一致时, 返回 -1.0D向量1 float 数组
     */
    public static Double calculateDistance(float[] a, float[] b, DistanceTypeEnum distanceType) {
        // 参数校验 - 确保两个向量有效
        if (Objects.isNull(a) || Objects.isNull(b) || !Objects.equals(a.length, b.length)) {
            log.error("Invalid input: At least one of a or b exists as null, or a and b are not of the same dimension, please check your input");
            return -1.0D;
        }
        // 参数校验 - 确保距离类型有效
        if (Objects.isNull(distanceType)) {
            log.error("Invalid input: distanceType is null, please check your input");
            return -1.0D;
        }
        // 根据距离类型调用对应的计算距离的方法
        return switch (distanceType) {
            case EUCLIDEAN -> calculateEuclideanDistance(a, b);
            case COSINE -> calculateCosineDistance(a, b);
            case OTHER -> calculateOtherDistance(a, b);
        };
    }

    /**
     * 计算距离的类型枚举类, 类型匹配: 1: 欧氏距离; 2: 余弦距离; 其他: 其他距离类型;
     */
    public enum DistanceTypeEnum {
        /**
         * 欧氏距离
         */
        EUCLIDEAN,
        /**
         * 余弦距离
         */
        COSINE,
        /**
         * 其他距离类型
         */
        OTHER;

        /**
         * 根据类型获取计算距离的类型
         *
         * @param type 距离类型, int 类型, 1: 欧氏距离; 2: 余弦距离; 其他: 其他距离类型;
         * @return 距离类型, 参见 {@link DistanceTypeEnum#EUCLIDEAN} 、{@link DistanceTypeEnum#COSINE} 、{@link DistanceTypeEnum#OTHER}
         */
        public static DistanceTypeEnum getByType(int type) {
            return switch (type) {
                case 1 -> EUCLIDEAN;
                case 2 -> COSINE;
                default -> OTHER;
            };
        }

        /**
         * 根据类型获取计算距离的类型名称
         *
         * @param type 距离类型, int 类型, 1: 欧氏距离; 2: 余弦距离; 其他: 其他距离类型;
         * @return 距离类型名称, String 类型
         */
        public static String getNameByType(int type) {
            return switch (type) {
                case 1 -> "欧式距离";
                case 2 -> "余弦距离";
                default -> "其他距离类型";
            };
        }
    }
}
