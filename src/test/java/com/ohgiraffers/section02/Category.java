package com.ohgiraffers.section02;

import javax.persistence.*;

@Entity(name="category_section02")
@Table(name="tbl_category")
@SqlResultSetMapping(
        name = "categoryCountAutoMapping2",
        entities = {@EntityResult(entityClass = com.ohgiraffers.section01.simple.Category.class)},
        columns = {@ColumnResult(name = "MENU_COUNT")}
)

//JPQL인지 NativeQuery인지 구분해서 어노테이션 달기
@NamedNativeQueries(
        value = {
                @NamedNativeQuery(
                        name = "Category.menuCountOfCategory",
                        query = "SELECT" +
                                "   A.CATEGORY_CODE, A.CATEGORY_NAME, A.REF_CATEGORY_CODE, NVL(V.MENU_COUNT, 0) MENU_COUNT" +
                                "                   FROM TBL_CATEGORY A" +
                                "                     LEFT JOIN (SELECT COUNT(*) AS MENU_COUNT, B.CATEGORY_CODE" +
                                "                                FROM TBL_MENU B" +
                                "                               GROUP BY B.CATEGORY_CODE) V ON (A.CATEGORY_CODE = V.CATEGORY_CODE)"+
                                "                     ORDER BY 1",
                        resultSetMapping = "categoryCountAutoMapping2"


                )
        }
)


public class Category {

    @Id
    private int categoryCode;
    private String categoryName;
    private Integer refCategoryCode;

    public Category() {
    }

    public Category(int categoryCode, String categoryName, Integer refCategoryCode) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.refCategoryCode = refCategoryCode;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getRefCategoryCode() {
        return refCategoryCode;
    }

    public void setRefCategoryCode(Integer refCategoryCode) {
        this.refCategoryCode = refCategoryCode;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryCode=" + categoryCode +
                ", categoryName='" + categoryName + '\'' +
                ", refCategoryCode=" + refCategoryCode +
                '}';
    }
}
