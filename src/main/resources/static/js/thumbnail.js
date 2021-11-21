


const myModal = new bootstrap.Modal(document.getElementById('thumbnailModal'), {keyboard: false});

const thumbBox = document.getElementById("thumbBox");
const uploadThumbBtn = document.getElementById("thumbnail");
const thumbDel = document.getElementById("thumbDelBtn");
const previewThumb = document.getElementById("thumbnailPreView");
const thumbUrl = document.getElementById("thumbnailUrl");
const thumbUrlUploadBtn = document.getElementById("thumbnail-url-upload-btn");

function uploadImg(input) {

    if(input.files && input.files[0]) {

        let token = getCsrfToken();
        let formData = new FormData();
        formData.append('img', input.files[0]);

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/article/uploadImg", false);
        xhr.setRequestHeader("contentType", "multipart/form-data");
        xhr.setRequestHeader("X-XSRF-TOKEN", token);
        xhr.send(formData);

        if (xhr.readyState === 4 && xhr.status === 200) {

            thumbUrl.value = xhr.response;
            previewThumb.src = thumbUrl.value;

            // 썸네일 등록은 서버에서 하도록 리팩토링할것
            // const reader = new FileReader();
            // reader.onload = e => {
            //     previewThumb.src = e.target.result;
            // }
            // reader.readAsDataURL(input.files[0])

            thumbBox.style.display = ''
            myModal.hide();

        } else {
            alert("이미지가 정상적으로 업로드되지 못했습니다.")
        }

    }
}



thumbUrlUploadBtn.addEventListener("click", () =>{
    const thumbUrlUploadInput = document.getElementById("thumbnail-url-upload-input");
    const url = thumbUrlUploadInput.value;
    previewThumb.src = url;
    thumbUrl.value = url;
    thumbBox.style.display = ''
    myModal.hide();
})

uploadThumbBtn.addEventListener("change", e => {
    uploadImg(e.target);
})

thumbDel.addEventListener("click", () =>{
    thumbBox.style.display = 'none';
    thumbUrl.value = "";
})