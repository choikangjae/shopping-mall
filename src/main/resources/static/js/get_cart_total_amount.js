window.addEventListener('load', (e) => {

        let data = {
            method: 'POST',
            // body: JSON.stringify({
            //     isSellerAgree,
            //     isLawAgree,
            //     isMandatoryAgree,
            //     isMarketingAgree
            // }),
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const onSuccess = res => {
            res.text()
            .then((data) =>
                document.getElementById('cartAmount').textContent = data
                );
        }
        const onFailure = res => {
            return res.json().then(json => alert(json.message));
        }

        fetch(`/api/v1/cart/total`, data)
            .then(res => {
                if (!res.ok) {
                    throw res;
                }
                return res
            })
            .then(onSuccess, onFailure)
            .catch(err => {
                alert(err.message);
            });
    })
