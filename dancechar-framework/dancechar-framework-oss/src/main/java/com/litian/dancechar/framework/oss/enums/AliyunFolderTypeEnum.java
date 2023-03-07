package com.litian.dancechar.framework.oss.enums;

/**
 * AliyunFolderTypeEnum 阿里云oss服务定义的文件夹类型
 */
public enum AliyunFolderTypeEnum {
    /**
     * 用户相关
     */
    USER("user"),
    CUSTOMER("customer"),
    APPLET("applet"),
    /**
     * 营销
     */
    MARKETING("marketing"),
    /**
     *协议
     */
    AGREEMENT("agreement"),
    /**
     *城市
     */
    CITY("city"),
    TEMP("temp");

    private String name;

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    AliyunFolderTypeEnum(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.toString() + "/";
    }

    public static AliyunFolderTypeEnum getFolderType(String folderName) {
        for (AliyunFolderTypeEnum typeEnum : AliyunFolderTypeEnum.values()) {
            if (typeEnum.getName().equals(folderName)) {
                return typeEnum;
            }
        }
        return AliyunFolderTypeEnum.TEMP;
    }

    @Override
    public String toString() {
        return name;
    }
}