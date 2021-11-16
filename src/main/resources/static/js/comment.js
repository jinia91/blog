document.addEventListener("DOMContentLoaded",
    function () {
        let replyBox = document.getElementById("commentBox");
        let token = getCsrfToken();
        let urlSearch = new URLSearchParams(location.search);
        let articleId = urlSearch.get("articleId");

        const xhr = new XMLHttpRequest();
        xhr.open("GET", "/comment/list/" + articleId);
        xhr.setRequestHeader("X-XSRF-TOKEN", token);
        xhr.send();

        xhr.onload = () => {
            makeCommentBox(xhr, replyBox);
        }
    }
);

function makeCommentBox(xhr, replyBox) {
    if (xhr.status === 200 || xhr.status === 201 || xhr.status === 202) {

        let commentList = JSON.parse(xhr.response);

        console.log(commentList);

        replyBox.innerHTML = '';
        // 부모 댓글 리스트
        for (const parentComment of commentList) {
            let replyHtmlSource = ' ';
            replyHtmlSource +=
              `<div class="mt-2 comment-box" id="commentBox">
                 <div class="d-flex flex-row p-2">
                   <div>
                     <img src="${parentComment.picUrl}" width="40" height="40"
                          class="rounded-circle me-2">
                   </div>
                   <div class="w-100">
                     <div class="d-flex justify-content-between align-items-center">
                       <span class="me-2">${parentComment.username}</span>
                       <div class="d-flex flex-row align-items-center">
                         <small>${parentComment.createdDate}</small>
                           <i class="fas fa-trash ms-3"></i>
                       </div>
                     </div>
                     <p class="text-justify comment-text mb-0">${parentComment.content}</p>
                                    <!-- comment-reply-post -->
                     <div class="comment-reply">
                       <div style="background-color: #fcf6f5;">
                         <button class="btn align-items-center rounded collapsed" data-bs-toggle="collapse"
                           data-bs-target="#comment-reply" aria-expanded="false">
                             <i class="fa fa-comments-o me-2"></i> 대댓글
                         </button>
                       </div>
                       <div class="collapse" id="comment-reply">
                           <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                               <li>
                               <div class="mt-3 d-flex flex-row align-items-center p-2 form-color">
                                 <div>
                                   <img src="${parentComment.picUrl}" width="50"
                                         height="50" class="rounded-circle me-2">
                                 </div>
                                 <textarea type="text" class="form-control" placeholder="대댓글을 작성해주세요..."></textarea>
                               </div>
                               <div class="d-flex">
                                 <div class="ms-auto ms-1 me-2">
                                   <button class="btn btn-sm btn-secondary">등 록</button>
                                 </div>
                               </div>
                             </li>
                           </ul>
                       </div>`


            // 자식 댓글 리스트
            for (const childComment of parentComment.commentDtoList) {
            replyHtmlSource +=
                      `<div class="d-flex flex-row p-2">
                        <div>
                          <img src="${childComment.picUrl}" width="40"
                            height="40" class="rounded-circle me-2">
                        </div>
                        <div class="w-100">
                          <div class="d-flex justify-content-between align-items-center">
                            <span class="me-2">${childComment.username}</span>
                            <div class="d-flex flex-row align-items-center">
                              <small>${childComment.createdDate}</small>
                              <i class="fas fa-trash ms-3"></i>
                            </div>
                          </div>
                          <p class="text-justify comment-text mb-0">${childComment.content}</p>
                        </div>
                       </div>`
            }

        // 부모댓글 리스트 마무리
        replyHtmlSource +=
                    `</div>
                   </div>
                 </div>
               </div>`
            //

            replyBox.innerHTML += replyHtmlSource;
        }


        // 댓글 작성창
       const container = document.getElementById("commentContainer");

        container.innerHTML +=
            `<div class="mt-3 d-flex align-items-center">
                <div>
                    <img src="[[${picUrl}]]" width="50"
                         height="50"
                         class="rounded-circle me-2">
                </div>
                <textarea type="text" class="form-control" placeholder="댓글을 작성해주세요..."></textarea>
            </div>
            <div class="d-flex">
                <div class="ms-auto mt-1 me-2">
                    <button class="btn btn-secondary btn-sm">등 록</button>
                </div>
            </div>`

    }
}