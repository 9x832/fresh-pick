package com.agrismart.agrimallbackend.common.util;

/**
 * 雪花算法（Snowflake）ID 生成器。
 *
 * 该类实现了 Twitter 开源的雪花算法，用于生成分布式系统中唯一的、有序的 64 位长整型 ID。
 * 生成的 ID 包含时间戳、数据中心 ID、机器 ID 和序列号，保证在分布式环境下 ID 的唯一性和有序性。
 *
 * ID 结构（64 位）：
 *
 * - 1 位符号位（始终为 0）
 * - 41 位时间戳（毫秒级，相对于起始时间）
 * - 5 位数据中心 ID（支持最多 32 个数据中心）
 * - 5 位机器 ID（每个数据中心支持最多 32 台机器）
 * - 12 位序列号（每毫秒最多生成 4096 个 ID）
 *
 * 特性：
 *
 * - 生成的 ID 按时间有序递增
 * - 在分布式环境下保证全局唯一
 * - 高性能，单机每毫秒可生成最多 4096 个 ID
 * - 无需依赖数据库或其他外部服务
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 创建 ID 生成器实例（数据中心 ID: 1, 机器 ID: 1）
 * SnowFlake snowFlake = new SnowFlake(1, 1);
 *
 * // 生成唯一 ID
 * long id = snowFlake.nextId();
 * }
 * </pre>
 *
 * 注意：
 *
 * - 数据中心 ID 和机器 ID 需要在分布式环境中唯一配置
 * - 如果系统时钟回拨，会抛出异常
 * - 同一毫秒内生成的 ID 通过序列号区分
 *
 * @author agrimall
 * @since 1.0
 */
public class SnowFlake {

    /**
     * 起始时间戳（2016-11-26 08:01:05）。
     * 用于计算时间戳的偏移量，减少 ID 长度。
     */
    private static final long START_STAMP = 1480166465631L;

    /**
     * 序列号占用的位数（12 位）。
     * 每毫秒最多可生成 2^12 = 4096 个 ID。
     */
    private static final long SEQUENCE_BIT = 12;

    /**
     * 机器 ID 占用的位数（5 位）。
     * 每个数据中心最多支持 2^5 = 32 台机器。
     */
    private static final long MACHINE_BIT = 5;

    /**
     * 数据中心 ID 占用的位数（5 位）。
     * 最多支持 2^5 = 32 个数据中心。
     */
    private static final long DATA_CENTER_BIT = 5;

    /**
     * 数据中心 ID 的最大值（31）。
     * 计算方式：2^5 - 1 = 31
     */
    private static final long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 机器 ID 的最大值（31）。
     * 计算方式：2^5 - 1 = 31
     */
    private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);

    /**
     * 序列号的最大值（4095）。
     * 计算方式：2^12 - 1 = 4095
     */
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 机器 ID 左移的位数。
     * 用于将机器 ID 放置到 ID 的正确位置。
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;

    /**
     * 数据中心 ID 左移的位数。
     * 用于将数据中心 ID 放置到 ID 的正确位置。
     */
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /**
     * 时间戳左移的位数。
     * 用于将时间戳放置到 ID 的正确位置。
     */
    private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    /**
     * 数据中心 ID。
     * 取值范围：0-31，在分布式环境中需要唯一配置。
     */
    private final long dataCenterId;

    /**
     * 机器 ID。
     * 取值范围：0-31，在同一数据中心内需要唯一配置。
     */
    private final long machineId;

    /**
     * 序列号。
     * 同一毫秒内的序列号，取值范围：0-4095。
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间戳。
     * 用于判断是否在同一毫秒内，以及检测时钟回拨。
     */
    private long lastStamp = -1L;

    /**
     * 构造函数，初始化数据中心 ID 和机器 ID。
     *
     * @param dataCenterId 数据中心 ID，取值范围：0-31
     * @param machineId    机器 ID，取值范围：0-31
     * @throws IllegalArgumentException 如果 dataCenterId 或 machineId 超出范围
     */
    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId out of range");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId out of range");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 生成下一个唯一 ID。
     *
     * 该方法使用同步机制保证线程安全，确保在并发环境下生成的 ID 唯一。
     *
     * ID 生成逻辑：
     *
     * - 获取当前时间戳
     * - 检测时钟回拨（当前时间小于上次时间）
     * - 如果同一毫秒内，序列号递增；否则序列号重置为 0
     * - 如果序列号溢出，等待下一毫秒
     * - 组合时间戳、数据中心 ID、机器 ID 和序列号生成最终 ID
     *
     * @return 生成的唯一 64 位长整型 ID
     * @throws IllegalStateException 如果检测到系统时钟回拨
     */
    public synchronized long nextId() {
        long currStamp = now();
        
        // 检测时钟回拨
        if (currStamp < lastStamp) {
            throw new IllegalStateException("Clock moved backwards");
        }

        // 同一毫秒内，序列号递增
        if (currStamp == lastStamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0L) {
                currStamp = nextMill();
            }
        } else {
            // 新的毫秒，序列号重置为 0
            sequence = 0L;
        }

        lastStamp = currStamp;

        // 组合各部分生成最终 ID
        return (currStamp - START_STAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    /**
     * 获取下一个毫秒时间戳。
     *
     * 当序列号溢出时，需要等待到下一个毫秒才能继续生成 ID。
     * 该方法会循环等待直到获取到大于上次时间戳的当前时间。
     *
     * @return 下一个毫秒的时间戳
     */
    private long nextMill() {
        long mill = now();
        while (mill <= lastStamp) {
            mill = now();
        }
        return mill;
    }

    /**
     * 获取当前时间戳（毫秒）。
     *
     * @return 当前时间戳（毫秒）
     */
    private long now() {
        return System.currentTimeMillis();
    }
}

