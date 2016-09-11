$(document).ready(function(){

    var ajaxError = function(jqXHR, textStatus, errorThrown){ alert(jqXHR.responseText) }
    var post = 'POST'
    var put = 'PUT'
    var del = 'DELETE'

    var tryRedirect = function(redirectInfo){
        try {
            var redirectJson = JSON.parse(redirectInfo)
            window.location.replace(redirectJson.redirect)
        } catch(e) {}
    }

    var handleData = function(data){
        setText(JSON.stringify(data, null, 2))
    }

    var setText = function(text){
        $('#inout').val(text)
    }

    var bindGet = function(elem, url){
        $(elem).click(function(){
            $.ajax({
                url: url,
                error: ajaxError
            }).then(handleData)
        });
    }

    var bindSend = function(elem, url, verb){
        $(elem).click(function(){
            $.ajax({
                    url: url,
                    type: verb,
                    contentType: "application/json",
                    data: $('#inout').val(),
                    error: ajaxError
            }).then(function(){setText('ok')})
        })
    }

    $('#logout').click(function(){
        var nonce = 'NONCE'
        $.ajax({
                url: '/logout',
                type: post,
                username: nonce,
                password: nonce,
                success: function () { alert('hmm. expected 401 here.'); },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status == 401)
                        tryRedirect(jqXHR.responseText)
                }
        })
    })


    bindGet('#createdb', '/app/createdb')
    bindSend('#createcustomer', '/app/addCustomer', post)

    bindGet('#getproducts', '/catalog/products')

    bindGet('#getbasket', '/shopping/basket')
    bindSend('#putbasket', '/shopping/basket/put', put)
    bindSend('#removebasket', '/shopping/basket/remove', del)
    bindGet('#submitorder', '/shopping/sendorder')


    bindGet('#getstock', '/stock/stock')
    bindSend('#newstockitem', '/stock/newstockitem', post)

    $.ajax({
        url: 'info/currentuser'
    }).then(function(userName){
        $('#greeting').text('Hello ' + userName + '!')
    })

});