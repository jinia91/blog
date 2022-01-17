# Jinia's LOG
나만의 블로그 웹 사이트 개발 프로젝트

https://www.jiniaslog.co.kr/

# 목차
- [개발 환경](#개발-환경)
- [사용 기술](#사용-기술)
    * [백엔드](#백엔드)
    * [프론트엔드](#프론트엔드)
    * [기타 주요 라이브러리](#기타-주요-라이브러리)
- [핵심 키워드](#핵심-키워드)
- [시스템 아키텍쳐](#시스템-아키텍쳐)
- [E-R 다이어그램](#e-r-다이어그램)
- [프로젝트 목적](#프로젝트-목적)
    * [블로그 프로젝트를 기획한 이유?](#블로그-프로젝트를-기획한-이유?)
- [핵심 기능](#핵심-기능)
    * [소셜 로그인](#소셜-로그인)
    * [반응형 웹](#반응형-웹)
    * [Toast Ui editor](#toast-ui-editor)
        + [글작성은 마크다운으로](#글작성은-마크다운으로)
        + [이미지와 썸네일 삽입시는 깃허브 이미지 서버로](#이미지와-썸네일-삽입시는-깃허브-이미지-서버로)
        + [트러블 슈팅: 컨텐츠 렌더링은 SSR으로](#트러블-슈팅~컨텐츠-렌더링은-SSR으로)
    * [기본적인 게시물 CRUD](#기본적인-게시물-CRUD)
    * [댓글과 대댓글 구현](#댓글과-대댓글-구현)
        + [트러블 슈팅 : 개행 반복 입력 문제](#트러블-슈팅~개행-반복-입력-문제)
    * [계층형 카테고리](#계층형-카테고리)
    * [카테고리 편집기 구현](#카테고리-편집기-구현)
    * [작성중인 게시물 자동저장](#작성중인-게시물-자동저장)
    * [에러처리](#에러처리)
    * [캐싱](#캐싱)
    * [게시물 포스팅시 깃헙 자동 백업](#게시물-포스팅시-깃헙-자동-백업)
    * [공유하기](#공유하기)
    * [태그검색, 키워드 검색](#태그검색,-키워드-검색)
    * [오프셋 페이징을 사용한 페이징박스와 커서 페이징을 사용한 무한 스크롤](#오프셋-페이징을-사용한-페이징박스와-커서-페이징을-사용한-무한-스크롤)
    * [CI/CD 무중단 배포](#CI/CD-무중단-배포)
    * [SEO 최적화](#seo-최적화)
        + [도메인과 https 프로토콜](#도메인과-https-프로토콜)
        + [SEO 최적화](#SEO-최적화)
- [프로젝트를 통해 느낀점](#프로젝트를-통해-느낀점)
- [프로젝트 관련 추가 포스팅](#프로젝트-관련-추가-포스팅)


## 개발 환경
- IntelliJ
- Postman
- GitHub
- SourceTree
- Mysql Workbench
- Visual Studio Code

## 사용 기술
### 백엔드
#### 주요 프레임워크 / 라이브러리
- Java 11 openjdk
- SpringBoot 2.5.6
- SpringBoot Security
- Spring Data JPA
- Mybatis
- EhCache

#### Build tool
- Gradle

#### Database
- Mysql

#### Infra
- AWS EC2
- AWS S3
- Travis CI
- AWS CodeDeploy
- AWS Route53

### 프론트엔드
- Javascript
- Html/Css
- Thymeleaf
- Bootstrap 5

### 기타 주요 라이브러리
- Lombok
- Github-api
- Toast Ui Editor

## 핵심 키워드

- 스프링 부트, 스프링 시큐리티를 사용하여 웹 애플리케이션 생애 주기 기획부터 배포 유지 보수까지 전과정 개발과 운영 경험 확보
- AWS / 리눅스 기반 CI/CD 무중단 배포 인프라 구축
- JPA, Hibernate를 사용한 도메인 설계
- MVC 프레임워크 기반 백엔드 서버 구축

## 시스템 아키텍쳐
![image](https://github.com/jinia91/blogBackUp/blob/main/img/57d1dfd7-22c1-4a5f-b6d5-ef635ae49307.png?raw=true)

## E-R 다이어그램
![image](https://github.com/jinia91/blogBackUp/blob/main/img/ff867940-efae-4040-ad47-5707e51d8865.png?raw=true)

## 프로젝트 목적

### 블로그 프로젝트를 기획한 이유? 

최소한의 구현능력을 학습한 이후 머릿속에 다양한 프로젝트 기획안들이 떠올랐습니다. 

욕심같아서는 머릿속의 기획들을 모두 만들어서 서비스해보고싶고, 높은 DAU도 찍어보고, 수익도 내보고 싶었지만 아직 쥬니어도 아닌 개발자 지망생으로서

우선은 **준비된 쥬니어 웹 개발자가 되자** 라는 눈앞의 목표를 최우선으로 삼고, 프로젝트안들을 다시 검토해 보았습니다.

 '**준비된 쥬니어 웹 개발자**가 되기위해 나는 무엇을 **준비**해야할까?' 

결론은 간단하게도 개발에 대한 끊임없는 공부와 열정.

그리고 그것들을 문서화하여 증명하고 공유하고 자기 PR을 하는 것이 저를 개발자 지망생이 아닌 개발자로 만들어줄 가장 빠르고 정확한 길이라고 생각했습니다. 

최초에는 깃헙 레포지토리에 단순히 자동 Publish를 하는 마크다운 편집기를 구상했으나

웹 개발자로서 웹 애플리케이션의 전 과정에대해 최소 한번쯤은 숙지를 해야한다고 판단했고,

개발 연습을 위한 개발, 포트폴리오를 위한 프로젝트로 남는것이 아닌 프로덕트 단계를 넘어, 

앞으로 제 프로그래밍 공부와 개발 기록, 그리고 유지보수를 같이할 블로그를 만들어 보기로 결정했습니다.


## 핵심 기능

### 소셜 로그인

소셜 로그인 구현을 위해 스프링 시큐리티와 OAuth2 인증방식을 사용했으며, 

소셜 인증 제공자 추가로 인한 확장을 대비해 엑세스 토큰으로 받아오는 유저 정보를 OAuth2UserInfo 인터페이스로 추상화하여 파싱하도록 설계했습니다.

[Oauth2UserInfo인터페이스](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/member/auth/userinfo/Oauth2UserInfo.java#L8)

또한 확장성 있는 객체 생성을 위해, 객체 생성을 담당하는 클래스를 익명 인터페이스를 사용한 팩토리 메서드 패턴으로 구현하였습니다. 

[UserInfoFactory 클래스](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/member/auth/UserInfoFactory.java#L18)

### 반응형 웹

부트스트랩을 이용하여 작은 모바일 환경은 물론 태블릿 대형 화면에서도 문제없이 작동하는 반응형 웹을 구현하였습니다.

![구글 서치콘솔 모바일 친화 인증](https://github.com/jinia91/blogBackUp/blob/main/img/bf4c2ba2-2446-47c1-bcdd-f69157bf4d29.png?raw=true)
[구글 서치 콘솔에서 모바일 친화페이지 인증]

### Toast Ui editor

#### 글작성은 마크다운으로

블로그의 글은 웹에 게시되는 컨텐츠인만큼 다양한 태그와 css를 지원하면서도 생산성있는 마크다운문법으로 작성되도록 하였습니다.

![마크다운 편집](https://github.com/jinia91/blogBackUp/blob/main/img/080e9414-2691-461f-b0d1-7590bf562e20.png?raw=true)

#### 이미지와 썸네일 삽입시는 깃허브 이미지 서버로

Toast Ui editor는 기본적으로 컨텐츠 내의 이미지 삽입을 blob으로 컨텐츠와 함께 병기하게 되는데 이경우 장황한 바이너리 코드로 DB에 부담이 되고

차후 검색엔진 최적화에 지장이 갈것으로 판단하여 

1. 이미지 삽입시 후킹으로 blob 코드를 낚아챈 후
2. 아작스로 해당 이미지 blob을 앱 서버로 보내기 
3. 앱서버에서 깃허브 api를 사용해 깃허브 레포지토리에 업로드
4. 그리고 이미지의 blob대신 업로드된 url을 반환

위 과정을 통해 깃헙 레포지토리를 이미지 서버로 활용하고 url만 사용하게끔 로직을 작성했습니다.

[아작스로 이미지 업로드](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/resources/static/js/thumbnail.js#L13)

#### 트러블 슈팅~컨텐츠 렌더링은 SSR으로

Toast Ui editor는 기본적으로 Toast Viewer라는  마크다운 파서를 제공하는데 

해당 라이브러리는 마크다운으로 작성된 컨텐츠가 클라이언트단에서 파싱되어 html로 렌더링되는 CSR으로 구현되어있습니다.

문제는 텍스트 컨텐츠가 메인인 블로그 웹앱에서 CSR을 사용해 컨텐츠를 그리게되면 웹 브라우저내에서 원할한 텍스트 인덱싱이 힘들어 향후 개발할 TOC 기능을 처리하기 힘들어지고

SEO 문제도 직면하게됨을 깨달았습니다. 이를 해결하기 위해 Viewer를 사용하지않고 서버내에서 마크다운을 파싱하는 SSR으로 구현하였습니다.

[서버단에서 마크다운 컨텐츠를 html로 파싱](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/article/controller/ArticleController.java#L269)


### 기본적인 게시물 CRUD

게시물에 대한 기본적인 CRUD를 모두 구현하였습니다.

글작성은 위의 내용처럼 마크다운을 통해 게시물이 등록되게 되며

글 읽기도 위의 내용처럼 마크다운을 html로 파싱후 렌더링되어 조회하게 됩니다.

수정과 삭제의 경우 관리자 role을 가진 계정만 인가하여 권한을 제한하였고 타임리프로 관리자 계정에만 해당 기능을 표시토록 하였습니다.

![수정 삭제기능 버튼](https://github.com/jinia91/blogBackUp/blob/main/img/114adecf-c67a-4420-b393-c40475cdce67.png?raw=true)

[관리자 계정에서만 보이는 글 수정 삭제 버튼]

![글 수정 화면](https://github.com/jinia91/blogBackUp/blob/main/img/921b1010-1b90-43d2-b21a-99bfa2737960.png?raw=true)
[글 수정 화면]

### 댓글과 대댓글 구현

댓글과 대댓글의 경우 엔티티 구조는 셀프조인으로 참조하되 계층 레벨을 표현하는 컬럼을 두어 구분토록하였고, 게시물에 달린 댓글들을 조회하는 경우

해당 댓글들을 백트래킹 알고리즘으로 계층화된 트리구조로 객체생성하여 대댓글은 물론 차후 정책에 따라 무한히 계층을 내려갈수 있도록 설계하였습니다.

[백트래킹 알고리즘으로 계층화 트리구조 생성 static factory method](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/comment/dto/CommentDto.java#L39)

#### 트러블 슈팅~개행 반복 입력 문제

또한 댓글 입력 방식을 textaria로 하였더니 개행이 저장되지 않고, 개행을 저장하도록 태그 수정을 했더니 개행이 문자열 1개자리만 차지하여 최대 255번의 개행만 입력될수 있는 문제점을 발견했습니다.

이를 해결하기 위해 입력받은 문자열중 개행이 2번이상 중복되는경우 1번으로 연쇄적으로 압축시키는 알고리즘을 작성하였습니다. 

[중복개행 연쇄제거 알고리즘](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/comment/service/CommentService.java#L101)

### 계층형 카테고리

카테고리 역시 위의 댓글처럼 계층 레벨을 표현하는 컬럼을 사용하고 셀프조인으로 계층을 형성하도록 엔티티 구조를 설계하였으며 레이아웃상에 카테고리를 보여주기 위하여

조회된 카테고리들을 백트래킹 알고리즘으로 계층화된 트리구조로 객체생성, 차후 정책에 따라 무한히 계층으로 표현할수 있도록 설계했습니다.

[백트래킹 알고리즘으로 계층화 트리구조 생성 static factory method](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/category/dto/CategoryForView.java#L38)


### 카테고리 편집기 구현

카테고리 추가, 삭제, 상위 카테고리로 이동, 하위 카테고리로 이동, 카테고리 순서 변경이 가능한 관리자용 API를 구현하여 블로그 운영을 보다 용이하게 만들었습니다.


![카테고리 편집](https://github.com/jinia91/blogBackUp/blob/main/img/bcd4b616-d5b9-4202-96ee-c6e243ad62a1.gif?raw=true)

[카테고리 편집 화면]

클라이언트단에서는 바닐라 자바스크립트를 통해 DTO를 수정하고 DOM을 조작하여 구현하였고 변경된 카테고리 리스트를 DTO로 백단으로 넘기면 

백단에서는 변경된 카테고리리스트와 기존 카테고리리스트 두개를 큐로 처리하여 비교대조를 통해 신규 카테고리생성, 기존카테고리 이름과 순서 변경, 카테고리 삭제 로직을 수행토록 했습니다.


### 작성중인 게시물 자동저장

글작성시 자바스크립트로 1분마다 아작스 요청을 통해 글을 임시 저장하는 기능을 추가했습니다.

![자동저장](https://github.com/jinia91/blogBackUp/blob/main/img/9cf13435-f570-4672-90f1-ac0810030b72.gif?raw=true)

### 에러처리

 로그인시 에러가 나는경우 이를 처리하기위해 커스텀 LoginFailHandler 클래스를 구현했으며, 기본적으로 외부 파라미터를 받는 컨트롤러 전면에 

Validated로 유효성 빈검사를 수행하여 실패 원자성을 유지하였습니다. 

또한 이러한 에러들을 ExceptionControllerAdvice가 받아 로깅을 남기도록 했고 에러화면을 출력하기 위해 커스텀 ExceptionController를 구현했습니다.

[예외처리 로직들](https://github.com/jinia91/blog/tree/main/src/main/java/myblog/blog/exception)

![에러화면](https://github.com/jinia91/blogBackUp/blob/main/img/bcb0c9a7-02e9-4137-b0cc-27d9c84c4f6b.png?raw=true)
[커스텀 에러화면]

### 캐싱

고정된 레이아웃상에 카테고리 목록들, 최신 댓글은 물론 메인화면상의 최신 게시물이나 인기게시물등 화면을 렌더링하기 위해 상당히 많은 데이터가 필요하고

해당 데이터들을 구하기위해 모든 클라이언트의 모든 조회시마다 DB에 쿼리를 날린다면 성능상으로 상당히 부하가 될것이라 판단하였습니다.

따라서 자주 사용되는 메서드들에 대하여 EhCache 를 통해 캐시 처리를 하고 만약 해당 캐시에 대한 데이터 정합성이 깨지는 메서드들을 사용할경우 

캐시를 폐기하는 정책을 사용하였습니다.

[캐시 설정 클래스](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/base/config/CacheConfig.java#L16)

또한 캐시 생명주기를 6시간으로 설정하여 별도의 메서드 없이도 6시간마다 캐시를 폐기하여 데이터 정합성을 유지하도록 설정했습니다.

### 게시물 포스팅시 깃헙 자동 백업

게시물을 작성할 경우 깃헙 api를 통해 해당 게시물을 백업용 레포지토리에 push하여 컨텐츠를 백업하도록 하였습니다.

[깃헙 push 로직](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/article/service/ArticleService.java#L182)
[깃헙 레포지토리, 백업 서버로 사용](https://github.com/jinia91/blogBackUp/tree/main)

### 공유하기

페이스북, 네이버 블로그, 카카오톡에 게시물을 공유하는 기능을 넣어 블로그의 접근성을 보다 높였습니다.

![카카오톡 공유](https://github.com/jinia91/blogBackUp/blob/main/img/22ca4613-b448-4401-8d24-60ffba06794c.png?raw=true)

[카카오톡 공유 예시]

### 태그검색, 키워드 검색

tagify 라이브러리를 사용하여 태그 기능을 구현하였고 태그나 게시물 컨텐츠의 특정 문자열에 대하여 검색하는 기능을 만들었습니다.

이때 보다 빠른검색을 위해 게시글의 내용 컨텐츠 타입을 varchar로 저장하고 인덱스를 걸었습니다.

검색 쿼리의 경우 "Like %s%" 를 사용하였기에 인덱스를 제대로 활용하여 레인지 스캔이 타지지는 않지만 인덱스 풀스캔은 타지기 때문에 인덱스가 없는것보다는 낫다고 판단했습니다.

차후 성능상 문제가 있을 경우 형태소 분석기를 설치하여 mysql의 FTS기능을 지원하도록 리팩토링할 예정입니다.

![](https://github.com/jinia91/blogBackUp/blob/main/img/8c600c59-fe7c-496a-9867-0d71288bb2b5.png?raw=true)
[검색 쿼리시 풀 인덱스 스캔이 타지는 모습]

### 오프셋 페이징을 사용한 페이징박스와 커서 페이징을 사용한 무한 스크롤

카테고리별 게시물 조회, 태그검색, 키워드 검색시 오프셋 페이징 쿼리를 이용해 페이징처리를 했으며 해당 페이지 넘버를 관리하기 위해

별도의 페이징 박스 핸들러 클래스를 만들어 페이징 박스를 렌더링 하도록 하였습니다.

또한 쿼리 파라미터를 통해 비정상적 페이지를 조회하는 케이스도 방지하도록 유효성 검사를 하였습니다.

[페이징 박스 핸들러](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/java/myblog/blog/article/dto/PagingBoxDto.java#L11)

메인화면의 경우 최신게시물순으로 5개씩 끊어서 스크롤 감지에 따라 다음 게시물들을 조회하는 무한 스크롤 페이징을 구현했으며

최초에는 토탈 카운트 쿼리를 날리지 않는 JPA의 Slice 인터페이스를 사용해서 페이징 처리를 했으나 

보다 성능개선을 위해 커서페이징으로 리팩토링하였습니다.

[자바스크립트 무한 스크롤 렌더링 로직](https://github.com/jinia91/blog/blob/a1d9381d8675ef01fbe3cf7371fe642a1847a943/src/main/resources/static/js/infinityScroll.js#L5)

[오프셋 페이징을 커서페이징으로 리팩토링하기](https://www.jiniaslog.co.kr/article/view?articleId=202)

### CI/CD 무중단 배포

애플리케이션 출시에 있어서 지속적 통합과 지속적 배포를 위해 깃헙, travis, AWS CodeDeploy를 사용했으며 빌드와 배포를 분리하기 위해 travis와 AWS s3를 이용했습니다.

![image](https://github.com/jinia91/blogBackUp/blob/main/img/57d1dfd7-22c1-4a5f-b6d5-ef635ae49307.png?raw=true)

깃헙으로 push된 프로젝트는 travis에서 설정에따라 자동화 테스트를 거쳐 빌드되며 빌드된 jar는 AWS S3에 저장됩니다. 

[travis 설정파일](https://github.com/jinia91/blog/blob/main/.travis.yml)


이후 배포요청을 받은 CodeDeploy는 S3에서 jar 파일을 넘겨받아 ec2로 파일을 넘겨주며 이때 배포 수명 주기의 순서대로 설정된 스크립트를 실행하게 됩니다.

[appspec 설정](https://github.com/jinia91/blog/blob/main/appspec.yml)

여기서 무중단 배포를 구현하기 위하여 EC2의 8081과 8082 포트에 프로젝트 jar 두개를 구동시키고 엔진엑스로 8080포트를 열되

엔진엑스는 81과 82중 하나의 포트만 리버스 프록시하도록 했습니다. 

이때 바라보지 않는 포트의 앱이 새로 배포되는 대상이 됩니다.

우선 새로 배포되는 앱을 AfterInstall 수명 주기에 멈춘뒤 ApplicationStart 주기에 해당 포트에 새로운 버전의 앱을 실행합니다.

이후 ValidateService 주기에 health.sh 스크립트로 정상배포를 확인하고 정상적일경우 엔진엑스의 프록시를 신규 버전 포트로 돌려(reload) 배포를 완료합니다.

[무중단 배포를 위한 쉘스크립트 파일들](https://github.com/jinia91/blog/tree/main/scripts)


### SEO 최적화

#### 도메인과 https 프로토콜

배포된 웹 앱을 제대로 서비스하기위해 letsencrypt로 ssl 인증을 받아 https 보안 프로토콜을 적용했으며, 가비아에서 도메인을 구입하고 AWS Route 53을 이용하여 도메인을 연결하였습니다.

#### SEO 최적화

1. RSS Feed 발행

앱 서버내에서 모든 게시물들을 조회한 뒤 해당 게시물들을 RSS 포멧에 맞는 xml로 작성하여 발행하도록 로직을 작성했으며 RSS 작성시 만들어지는 객체생성과 게시물 조회의 비용을 생각하여

해당 로직을 캐싱처리토록 했습니다.

2. sitemap

또한 현재 존재하는 카테고리들의 url을 이용해 sitemap xml을 동적으로 작성하도록 하였으며 역시나 캐싱처리를 통해 비용에 대한 부담을 경감시켰습니다.

3. 동적 메타태그 작성

모든 게시물들은 해당 컨텐츠를 이용해 서버사이드에서 동적으로 메타태그를 작성토록하여 seo 최적화를 구축했습니다.

4. robot설정

크롤링대상이 되는 url과 조회할 필요없는 url을 분류하여 robot.txt를 작성했습니다.

5. 네이버 검색 어드바이저와 구글 서치콘솔에 등록

각 검색 서비스에 소유 인증을 하고 rss와 sitemap 제출을 하여 적극적인 크롤링 요청을 하였습니다.

![검색](https://github.com/jinia91/blogBackUp/blob/main/img/1aacfe5e-a6d3-47c8-80d1-b1f4165bd85e.png?raw=true)

[구글 검색창에서 조회결과]

## 프로젝트를 통해 느낀점

1인 프로젝트로 진행하다보니 제가 짠 코드가 정말 괜찮은 코드인지, 설계를 이렇게 하는게 과연 맞는건지, 더 좋은 구현방법은 없는지 정말 많은 고민을 하였습니다.

비전공이고 프로그래밍 공부를 시작한지 6개월도 채 안되어 모든것이 처음이고 낯설게 느껴졌으며 프로젝트 기간동안 깜깜한 미궁속을 헤메는 느낌도 받았습니다. 

하지만 프로젝트를 진행하는 기간동안 정말 하루하루가 빠르게 지나갔고, 어려운 문제를 맞닥뜨릴때 이를 해결하기위해 18시간동안 자리에 내리 앉아 골머리를 싸매며 코딩하거나

배포를 하기위해 10시간동안 40번씩 커밋하며 문제를 해결하려는 제 모습을 발견하면서 

**나는 개발에 몰입할 수 있다, 어떤 문제라도 해결할수 있다**는 자신감을 갖게 되었습니다.

또한 아직 부족한 것이 너무 많고, 공부를 하면 할수록 새로운 공부거리가 몇배로 새로 나오고 있지만 그 모든 공부 과정이 힘들고 고통스럽기 보다는 할만하다, 재밌다는 느낌을 살면서 처음 받았습니다.

이번 프로젝트로 정말 많은것을 공부하고 배웠고, 문제에 직면했을때 이를 해결했던 크고 작은 경험들이 저에게 큰 자산이 되었다고 확신합니다.


## 프로젝트 관련 추가 포스팅

- [스프링 JPA 환경에서 오프셋 페이징을 커서 페이징으로 개선하기](https://www.jiniaslog.co.kr/article/view?articleId=202)
- [스프링에서 캐시 사용하여 프로젝트 성능 개선해보기](https://www.jiniaslog.co.kr/article/view?articleId=254)
- [[CI/CD 무중단배포 프로젝트 적용하기] CI? CD? 기본 개념잡기 (1)](https://www.jiniaslog.co.kr/article/view?articleId=303)
- [운영환경에서 정적 리소스의 버전관리와 브라우저의 캐시 문제](https://www.jiniaslog.co.kr/article/view?articleId=402)