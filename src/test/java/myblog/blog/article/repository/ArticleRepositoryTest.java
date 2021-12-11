package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.BatchUpdateException;
import java.util.List;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;


    @BeforeEach
    void 카테고리더미삽입(){

        // 카테고리 삽입

        // 부모 카테고리
        Category category1 = Category.builder()
                .tier(1)
                .title("카테고리 부모")
                .pSortNum(1)
                .cSortNum(0)
                .build();
        categoryRepository.save(category1);

        // 자식 카테고리
        Category category2 = Category.builder()
                .tier(2)
                .title("카테고리 자식")
                .pSortNum(1)
                .cSortNum(1)
                .parents(category1)
                .build();
        categoryRepository.save(category2);

        // 멤버 삽입은 postConstruct로


    }

    @Test
    void 글작성성공테스트() throws Exception {

        Category category2 = categoryRepository.findByTitle("카테고리 자식");
        Member admin = memberRepository.findById(1L).get();

        Article newArticle = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();


        // when

        Article savedArticle = articleRepository.save(newArticle);
        Long articleId = savedArticle.getId();

        entityManager.flush();
        entityManager.clear();

        // then

        Assertions
                .assertThat(articleRepository.findById(articleId).isPresent()).isTrue();

        Assertions.assertThat(savedArticle.getContent())
                .isEqualTo(articleRepository.findById(articleId).get().getContent());

    }

    @Test
    public void 글작성실패() throws Exception {
        // given
        Category category2 = categoryRepository.findByTitle("카테고리 자식");
        Member admin = memberRepository.findById(1L).get();

//      타이틀 없는 아티클
        Article nullTitle = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
//                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        //썸네일 없는 아티클
        Article nullThumbnail = Article.builder()
                .category(category2)
                .content("테스트")
//                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 내용 없는 아티클
        Article nullContents = Article.builder()
                .category(category2)
//                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 카테고리 없는 아티클
        Article nullCategory = Article.builder()
//                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 작성자 없는 아티클
        Article nullWriter = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
//                .member(admin)
                .toc(null)
                .build();


        // when

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullTitle);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullThumbnail);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullContents);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullCategory);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullWriter);
                    entityManager.flush();
                });

    }

    @Test
    @Rollback(value = false)
    @Transactional
    public void 인덱스스캔테스트() throws Exception {
    // given
        Category category = categoryRepository.findByTitle("카테고리 자식");
        Member admin = memberRepository.findById(1L).get();

        Article articleSample = Article.builder()
                .category(category)
                .content("## 1. 네트워크 기초\n" +
                        "\n" +
                        "### 공부 목적\n" +
                        "백엔드 개발자로 진로를 정하고 공부를 하면서 비전공자로서 CS의 얕은 지식에 한계를 많이 느끼고 있다. \n" +
                        "\n" +
                        "서블릿과 스프링을 통해 웹 애플리케이션을 만들면서 당장에 직면하는 http 프로토콜과 그 밑으로 숨겨져있는 데이터 송수신의 과정들, IP와 DNS 작동원리 등등\n" +
                        "\n" +
                        "아니 그런것을 떠나서 **인터넷**이라는게 뭔지조차 한두문장으로 깔끔하게 설명 못하는 내 모습이 한심하게 느껴져서 공부를 시작하고자 한다.\n" +
                        "\n" +
                        "\n" +
                        "### (1) 인터넷이란?\n" +
                        "\n" +
                        "인터넷을 이야기하기에 앞서 **네트워크**에 대해 먼저 정의하자.\t\n" +
                        "\n" +
                        "**네트워크**란 Net + Work의 합성어로 일하는 그물, 그물처럼 엮여서 일하는 것이란 의미로 직역해볼수 있으며, 정보통신 영역에서는 원하는 정보를 원하는 수신자에게 정확히 전송하기 위한 기반 인프라라고 설명할수 있다.\n" +
                        "\n" +
                        "여기서 정보를 송수신하는 단말(노드)은 컴퓨터, 핸드폰 등등 다양한 종류가 있지만 설명의 편의를 위해 컴퓨터로 통칭하고 이를 사용하는 네트워크, 즉 **컴퓨터 네트워크**를 앞으로 네트워크로 약칭하겠다.\n" +
                        "\n" +
                        "네트워크는 연결된 노드의 수에따라 크고 작은 네트워크로 구분될수 있는데 **인터넷**은 *TCP/IP 프로토콜*을 기반으로 전세계적으로 연결된 네트워크를 일컫는 말이다. 여기서 인터넷을 브라우저에서 구동되는 웹으로 한정지어 생각하기 쉬운데, 인터넷은 웹은 물론, 전자 메일, 동영상스트리밍, 온라인 게임 VoIP등 다양한 서비스를 포함하는 말이다.\n" +
                        "\n" +
                        "\n" +
                        "### (2) 랜(LAN)과 왠(WAN)\n" +
                        "\n" +
                        "인터넷의 이야기에서 네트워크로 다시 돌아와보자. \n" +
                        "\n" +
                        "네트워크는 규모에따라 분류해볼수 있는데, 대표적으로 LAN과 WAN에대해 살펴보겠다.\n" +
                        "![enter image description here](https://ww.namu.la/s/30118c345e47ffe378f33bb37dbdb9372e7cafd61e54c07d1b5832f52742b4b0b2807d557bd1bfc3ec3780f915773a9958845b2565add20510be3e193e53e8acb2f39f6529970f7b888ad7a5a212e19f54c1377ee9f59d70cc3f93189d1c0e71)\n" +
                        "LAN(Local Area Network)은 근거리 통신망의 약어로 근거리의 범위는 따로 정해진건 없지만, 일반적으로 하나의 사무실 혹은 주택내의 네트워크를 LAN이라고 부른다.\n" +
                        "\n" +
                        "근거리 통신망? 주택내의 네트워크? 뭔가 와닿지 않을수 있는데, 집에 설치된 인터넷 공유기를 떠올리면 쉽게 이해가 될것이다.\n" +
                        "\n" +
                        "**외부(ISP-인터넷서비스제공자)**에서 **인터넷회선**을 통해 **인터넷공유기(Broadband Router)**로 인터넷을 연결시키면, 이 공유기를 중심으로 단말기들과 인터넷망(사설망)이 구성되는데 이것이 바로 랜이다.  \n" +
                        "\n" +
                        "위의 그림에는 컴퓨터 4대가 랜으로 묶여있지만, 컴퓨터를 비롯해서 와이파이로 연결되는 핸드폰, 게임기, 노트북 등등 이 모든 단말기들이 하나의 랜(무선랜, 유선랜)으로 불릴수 있는것이다.\n" +
                        "\n" +
                        " WAN(Wide Area Network)은 광역 통신망의 약어로, 수많은 랜을 연결하여 가장 상위에 속하는 통신망이며 ISP(KT,SK브로드밴드 등)가 인터넷 서비스를 제공하기 위해 전국에 회선을 깔아 구축한 통신망이 대표적인 예이다.\n" +
                        "\n" +
                        "\n" +
                        "### (3) 통신규약(Protocol)\n" +
                        "\n" +
                        "우리가 편지를 보내기 위해서는 무엇을 해야할까? 편지를 쓰고 보내는 과정을 정리해보자.\n" +
                        "\n" +
                        "1. 우선 아무 편지지나 종이쪼가리에 글을 쓸것이다. \n" +
                        "2. 편지 봉투에 편지를 담고, 편지봉투에 **정해진 우표**와 보내는이, 그리고 받는이를 **정해진 우편번호**로 적는다.\n" +
                        "3.  편지봉투를 **정해진 우체통**에 넣는다.\n" +
                        "\n" +
                        "자 여기까지만 살펴보더라도 우리는 우리가 쓴 편지(DATA)를 원하는 상대에게 보내기 위해 상당히 많은 **규칙**을 지키고 있음을 확인할 수 있다.\n" +
                        "\n" +
                        "우선 정해진 우표를 사서 붙임으로서 우편요금을 내고, 정해진 우편번호를 적음으로서 송신자와 수신자가 누군지 식별가능케 한다. 그리고 정해진 우체통에 편지를 넣음으로서  편지를 다룰 다음 사람(우편배달부)에게 전송을 완료한다.\n" +
                        "\n" +
                        "**통신 네트워크도 위와 다르지 않다.**\n" +
                        "\n" +
                        "우리가 작성하고 만든 데이터 혹은 데이터를 달라하는 요청을 누군가에게 송신하려면 수많은 네트워크 규칙을 준수해야하고, 그 규칙하에 데이터(요청)은 수신자에게 전달될 것이다.\n" +
                        "\n" +
                        "이러한 규칙을 프로토콜(Protocol)이라고 부른다.\n" +
                        "\n" +
                        "\n" +
                        "### (4) OSI모델과 TCP/IP 모델\n" +
                        "\n" +
                        "앞서 프로토콜에 대해 알아보았다. 그런데 만약 서울에서 사용하는 WAN과 부산의 WAN 간의 프로토콜이 다르다면? 혹은 강서구와 강동구간의 프로토콜이 다르다면? 각기 네트워크간의 프로토콜이 모두 다르다면?\n" +
                        "\n" +
                        "서로다른 프로토콜을 통역하기위해 수많은 전처리과정을 거쳐야하고 어마어마한 비효율을 낳을 것이다.\n" +
                        "\n" +
                        "이를 해결하기 위해 등장한 것이 **표준 프로토콜 규격** OSI 모델과 TCP/IP 모델이다.\n" +
                        "\n" +
                        "\n" +
                        "#### OSI 모델\n" +
                        "\n" +
                        "OSI 모델은 ISO(International Organization for Standardization 국제표준화기구)에서 제정한 컴퓨터 네트워크 표준 프로토콜 규격으로\n" +
                        "\n" +
                        "통신네트워크를 7계층(Layer)으로 나누어 설명하고, 각각의 프로토콜을 정의한다.\n" +
                        "\n" +
                        "![enter image description here](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrGB2x1KXViaU-GJD61bW840ynAnUReo2TpA&usqp=CAU)\n" +
                        "\n" +
                        "|계층|이름|\n" +
                        "|-|-|-|\n" +
                        "|7계층|응용(Application)|\n" +
                        "|6계층|표현(Presentation)||\n" +
                        "|5계층|세션(Session)||\n" +
                        "|4계층|전송(Transport)||\n" +
                        "|3계층|네트워크(Network)||\n" +
                        "|2계층|데이터 링크(Data Link)||\n" +
                        "|1계층|물리(Physical)||\n" +
                        "\n" +
                        "#### TCP/IP\n" +
                        "\n" +
                        "![enter image description here](https://ww.namu.la/s/0aa9f4305d7fbcbb9bc932e86e9dc9318c5ab4c6f8adfcbf9f90e1b2a21c1d852cba07676ee2b5830979280bd20e0ff016d5981f804fa410bb1ea302a88625345256c0496e049ca2ea4840132da24f29080b75d498a813fdab24bdcb4be3422f99067b0a36f7ae1f34c57215a2deb446)\n" +
                        "\n" +
                        "현재 수많은 프로그램들이 인터넷으로 통신하는데 있어서 가장 많이 기반으로 삼는 프로토콜은 TCP와 IP이다. \n" +
                        "\n" +
                        "TCP/IP는 최초의 컴퓨터 네트워크였던 알파넷에서 사용하기 시작하였고 UNIX의 기본 프로토콜로 사용되었으며 현재 인터넷 범용 프로토콜이 되었기에, 인터넷 프로토콜 그 자체를 표현하는 용어기도 하며 크게 4계층으로 나뉜다.\n" +
                        "\n" +
                        "|계층|이름|\n" +
                        "|-|-|\n" +
                        "|4계층|응용(Application)||\n" +
                        "|3계층|전송(Transport)||\n" +
                        "|2계층|인터넷(Internet)||\n" +
                        "|1계층|네트워크 접속(Link)||\n" +
                        "\n" +
                        "위의 그림처럼 TCP/IP 모델은 OSI모델과 유사하게 매핑이 가능하다. 여기서 네트워크 접속계층을 OSI처럼 두개로 나누어 \n" +
                        "\n" +
                        "|계층|이름|\n" +
                        "|-|-|\n" +
                        "|5계층|응용(Application)||\n" +
                        "|4계층|전송(Transport)||\n" +
                        "|3계층|인터넷(Internet)||\n" +
                        "|2계층|데이터 링크(Link)||\n" +
                        "|1계층|물리(Physical)||\n" +
                        "\n" +
                        "처럼 5계층으로 나누기도하는데 \n" +
                        "\n" +
                        "앞으로 위의 TCP/IP 5계층 모델을 가지고 각 계층별로 자세히 살펴보며 네트워크의 전체 흐름을 공부해보겠다.")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        articleRepository.save(articleSample);


        for(int i =0; i<1000; i++) {
            Article newArticle = Article.builder()
                    .category(category)
                    .content(articleSample.getContent())
                    .thumbnailUrl("더미")
                    .title("테스트")
                    .member(admin)
                    .toc(null)
                    .build();
            articleRepository.save(newArticle);
        }

        // when

    // then
    }




}