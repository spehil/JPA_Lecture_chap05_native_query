package com.ohgiraffers.section02;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NamedQueryTests {


    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll
    public static void initFactoty() {

        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");

    }

    @BeforeEach//EntityManager는 매번 만들어져야하므로 test하나가 수행되기 전마다 수행되는 BeforeEach 작성
    public void initManager() {

        entityManager = entityManagerFactory.createEntityManager();

    }

    @AfterAll //BeforeAll과 AfterAll은 static으로 작성한다.
    public static void closeFactory() {

        entityManagerFactory.close();
    }


    @AfterEach
    public void closeManager() {
        entityManager.close();
    }

    @Test
    public void namedNativeQuery를_이용한_조회_테스트(){
        //when
        Query nativeQuery = entityManager.createNamedQuery("Category.menuCountOfCategory");
        List<Object[]> categoryList = nativeQuery.getResultList();
        //then
        assertNotNull(categoryList);
        assertTrue(entityManager.contains(categoryList.get(0)[0]));//포함된다는건 엔티티는 영속성컨텍스트에서 전부 다 관리된다고 보면된다.
        categoryList.forEach(row->{
            Stream.of(row).forEach(col -> System.out.print(col + " "));
            System.out.println();
        });

    }

}
