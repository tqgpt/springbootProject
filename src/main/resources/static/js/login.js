const param = new URLSearchParams(window.location.search);
const form = document.querySelector('.login_form');


if (param.has('error')) {
    alert('아이디 또는 비밀번호가 틀렸습니다.');
}


form.addEventListener('submit', e => {
    e.stopPropagation();
    e.preventDefault();
    submitForm();
});


function submitForm() {
    form.submit();
}
