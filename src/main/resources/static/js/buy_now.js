window.addEventListener('load', (e)=> {
document.getElementById('buyNow').addEventListener('click',() => {
    alert('Buy Now')
})
})

    // window.addEventListener('load', (e)=> {
    //     const agreeButton = document.getElementById('agree-button');

    //     agreeButton.addEventListener('click', (e) => {
    //         // e.preventDefault();

    //         let isMandatoryAgree = document.getElementById('mandatory-check').checked;
    //         let isMarketingAgree = document.getElementById('marketing-check').checked;

    //         let agreeData = {
    //             method: 'POST',
    //             body: JSON.stringify({
    //                 isMandatoryAgree,
    //                 isMarketingAgree
    //             }),
    //             headers: {
    //                 'Content-Type': 'application/json'
    //             }
    //         };

    //         fetch(`/me/privacy/agree`, agreeData)
    //         .then(res => {
    //             if (!res.ok) {
    //                 alert('필수 항목은 반드시 체크해야합니다');
    //             }
    //             if (res.ok) {
    //                 window.location.replace('http://localhost:8080/me/update')
    //             }
    //             res.json();
    //         })
    //         .then(json => {
    //             console.log(json)
    //         })
    //         .catch(err => {
    //             alert(err.message);
    //         });
    //     })
    // })
