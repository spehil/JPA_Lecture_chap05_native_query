package com.ohgiraffers.section01.simple;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.List;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NativeQueryTests {



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
    public void 결과_타입을_정의한_네이티브_쿼리_사용_테스트(){
        //given
        int menuCodeParameter = 15;
        //오라클에서 구동하는 오라클사용시 쿼리로 작성 (JPQL이 아님)
        //엔티티로 조회시 모든 컬럼을 써서 조회해야한다.
        //when
//        String query = "SELECT MENU_CODE, MENU_NAME, MENU_PRICE" +
//                   " FROM TBL_MENU WHERE MENU_CODE =?";//열이름이 부적합니다라는 에러가 난다. Menu foundMenu = (Menu)nativeQuery.getSingleResult();
        //주의!!!!!엔티티로 조회시 모든 컬럼을 써서 조회해야한다.
        //유의사항. 해당엔티티의 값을 전부 작성해서 가져와야 한다. 위치기반홀더로 작성야한다.!!!  -> .setParameter(1,menuCodeParameter);

        String query = "SELECT MENU_CODE, MENU_NAME, MENU_PRICE, CATEGORY_CODE, ORDERABLE_STATUS" +
                " FROM TBL_MENU WHERE MENU_CODE =?";//위치기반 위치홀더로 작성해준다.
        //네이티브 쿼리는  엔티티로 얻어오는것으로 영속성컨텍스트에서 관리한다 .
        Query nativeQuery = entityManager.createNativeQuery(query, Menu.class)
                .setParameter(1,menuCodeParameter);//Menu.class수행후 반환받을 엔티티타입
        Menu foundMenu = (Menu)nativeQuery.getSingleResult();//사용할타입으로 다운캐스팅함.
        //then
        assertNotNull(foundMenu);
        assertTrue(entityManager.contains(foundMenu));//네이티브쿼리로 작성한 결과값은 엔티티로 영속성 컨텍스트에서 관리된다 (포함된다는건 영속성컨텍스트에서 관리된다는뜻)
        System.out.println(foundMenu);
        }

        @Test
    public  void 결과_타입을_정의할_수_없는_경우_테스트(){

        //when
            String query = "SELECT MENU_NAME, MENU_PRICE FROM TBL_MENU";
            List<Object[]> menuList = entityManager.createNativeQuery(query).getResultList();//주의 !!!!수행할 쿼리만 전달해준다. 엔티티 조회가 아니라 타입지정은 해주지 않는다.
          // List<Object[]> menuList = entityManager.createNativeQuery(query, Object[].class).getResultList();//createNativeQuery에서 엔티티 아닌걸로 조회시에는 타입지정이 안됨
           //엔티티 조회가 아닌경우 타입지정을 하면 에러가 난다. 그래서 수행할 쿼리만 전달해준다.
            //then
            assertNotNull(menuList);
            menuList.forEach(row -> { //Object[]을 옆으로 출력하겠다는 의미로 코드 작성
                Stream.of(row).forEach(col -> System.out.print(col + " "));
                System.out.println();
            });

        }

        @Test
    public void 자동_결과_매핑을_사용한_조회_테스트(){

            //resultset을 만들어서 매핑한다는 코드 작성
            //when
            String query = "SELECT" +
                    " A.CATEGORY_CODE, A.CATEGORY_NAME, A.REF_CATEGORY_COED, NVL(V.MENU_COUNT, 0) MENU_COUNT" +
                    " FROM TBL_CATEGORY A" +
                    " LEFT JOIN (SELECT COUNT(*) AS MENU_COUNT, B.CATEGORY_CODE)" +
                    "            FROM TBL_MENU B" +
                    "            GROUP BY B.CATEGORY_CODE V ON (A.CATEGORY_CODE = V.CATEGORY_CODE)" +
                    " ORDER BY 1";

            Query nativeQuery = entityManager.createNativeQuery(query, "categoryCountAutoMapping");
            List<Object[]> categoryList = nativeQuery.getResultList();//하나의 엔티티와 하나의 컬럼이 배열에 전달되기때문에 오브젝트 배열로 코드 작성

            //then
            assertNotNull(categoryList);
            assertTrue(entityManager.contains(categoryList.get(0)[0]));//포함된다는건 엔티티는 영속성컨텍스트에서 전부 다 관리된다고 보면된다.
            categoryList.forEach(row->{
                Stream.of(row).forEach(col -> System.out.print(col + " "));
                System.out.println();
            });


        }

    @Test
    public void 메뉴얼_매핑을_사용한_조회_테스트(){

        //resultset을 만들어서 매핑한다는 코드 작성
        //when
        String query = "SELECT" +
                " A.CATEGORY_CODE, A.CATEGORY_NAME, A.REF_CATEGORY_COED, NVL(V.MENU_COUNT, 0) MENU_COUNT" +
                " FROM TBL_CATEGORY A" +
                " LEFT JOIN (SELECT COUNT(*) AS MENU_COUNT, B.CATEGORY_CODE)" +
                "            FROM TBL_MENU B" +
                "            GROUP BY B.CATEGORY_CODE V ON (A.CATEGORY_CODE = V.CATEGORY_CODE)" +
                " ORDER BY 1";

        Query nativeQuery = entityManager.createNativeQuery(query, "categoryCountAutoMapping");
        List<Object[]> categoryList = nativeQuery.getResultList();//하나의 엔티티와 하나의 컬럼이 배열에 전달되기때문에 오브젝트 배열로 코드 작성

        //then
        assertNotNull(categoryList);
        assertTrue(entityManager.contains(categoryList.get(0)[0]));//포함된다는건 엔티티는 영속성컨텍스트에서 전부 다 관리된다고 보면된다.
        categoryList.forEach(row->{
            Stream.of(row).forEach(col -> System.out.print(col + " "));
            System.out.println();
        });

    }
}


