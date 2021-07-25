package com.zwl.phoenix.defender.phoenix;

/**
 * 字段类型
 *
 * @author Future.Zhang
 * @date 2021-03-30
 **/
public enum PhoenixDataTypeEnum {

    DECIMAL(3, "DECIMAL"),
    INTEGER(4, "INTEGER"),
    VARCHAR(12, "VARCHAR"),
    DATE(91, "DATA"),
    TIMESTAMP(93, "TIMESTAMP");


    private Integer type;
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static PhoenixDataTypeEnum getDataTypeEnum(Integer type) {
        for(PhoenixDataTypeEnum dataTypeEnum : PhoenixDataTypeEnum.values()){
            if (dataTypeEnum.getType().equals(type)) {
                return dataTypeEnum;
            }
        }

        return null;
    }

    PhoenixDataTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
