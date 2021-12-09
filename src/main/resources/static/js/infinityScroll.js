let pageNum = 1;
let flag = false;
const container = document.getElementById("infiniteScrollBox");

function InfinityScroll() {
    const next = document.getElementById("nextPagination");
    const screenHeight = screen.height;

    document.addEventListener('scroll', OnScroll, {passive: true})

    function OnScroll() {
        const fullHeight = container.clientHeight;
        const scrollPosition = pageYOffset;
        if (fullHeight - screenHeight / 2 <= scrollPosition && !flag) {
            flag = true;

            makeNextPage();

        }
    }
}

function makeNextPage() {
    const xhr = new XMLHttpRequest();
    // 페이지 요청보내기
    xhr.open('GET', "/main/article/" + pageNum);
    // xhr.setRequestHeader("X-XSRF-TOKEN", token);
    xhr.send();

    xhr.onload = () => {

        if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201 || xhr.status === 202) {


                let list = JSON.parse(xhr.response);

                console.log(list);

                if(list.length === 0) return;

                // 다음 페이지 작성
                const nextPage = document.createElement('div');
                nextPage.setAttribute("id", "articlePage-" + pageNum++);
                // 아티클 개별추가
                for (let listElement of list) {
                    let article = document.createElement('div');
                    let date = moment(listElement.createdDate).format('YYYY-MM-DD');

                    let articleHtmlSource = ' ';
                    articleHtmlSource +=
                        `<div class=\"card mb-3 recent-card wow fadeInUp ">
                                            <a href="/article/view?articleId=${listElement.id}">
                                                <div class="row g-0">
                                                    <div class="col-3">
                                                        <div class="ratio ratio-1x1\" style="background-image: url(${listElement.thumbnailUrl}); background-size: cover;"></div>
                                                    </div>
                                                    <div class="col-9 row row-cols-1 align-self-center">
                                                        <h3 class="card-title col mb-3 text-truncate">${listElement.title}</h3>
                                                        <p class="col mb-0"><small class="text-muted">${date}</small></p>
                                                    </div>
                                                </div>
                                            </a>
                        </div>`

                    article.innerHTML = articleHtmlSource;
                    nextPage.appendChild(article);
                }

                container.appendChild(nextPage);

                flag = false;
            } else {
                console.error(xhr.response);
            }
        }
    }
}

InfinityScroll();
new WOW().init();