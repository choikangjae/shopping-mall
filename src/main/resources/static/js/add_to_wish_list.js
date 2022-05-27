function addToWishList(itemId) {

                    let data = {
                        method: 'POST',
                        body: JSON.stringify({
                            itemId,
                        }),
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    };

                    const onSuccess = res => {
                        res.json()
                        .then((data) => {
                            document.getElementById('zzim' + itemId).textContent = data.zzimPerItem
                            if (data.isZzimed) {
                                document.getElementById('heart' + itemId).classList.add('text-danger')
                            } else {
                                document.getElementById('heart' + itemId).classList.remove('text-danger');
                            }
                        });
                    }
                    const onFailure = res => {
                        if (confirm("로그인이 필요합니다")) {
                            window.location.replace('http://localhost:8080/auth/login');
                        }
                        // return res.text().then(json => alert(json.message));
                    }

                    fetch(`/api/v1/item/zzim`, data)
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
                }