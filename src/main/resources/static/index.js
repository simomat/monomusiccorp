$(document).ready(function(){

    $.ajaxSetup({
        error: function(jqXHR, textStatus, errorThrown){
            alert(jqXHR.responseText)},
        cache: false
    })

    var post = 'POST'
    var put = 'PUT'
    var del = 'DELETE'

    var handleData = function(data){
        setText(JSON.stringify(data, null, 2))
    }

    var setText = function(text){
        $('#inout').val(text)
    }

    var bindGet = function(elem, url){
        $(elem).click(function(){
            $.ajax({url: url}).then(handleData)
        });
    }

    var bindSend = function(elem, url, verb){
        $(elem).click(function(){
            $.ajax({
                    url: url,
                    type: verb,
                    contentType: "application/json",
                    data: $('#inout').val()
            }).then(function(){setText('ok')})
        })
    }

    bindGet('#test', '/app/')
    bindGet('#createdb', '/app/createdb')
    bindSend('#createcustomer', '/app/addCustomer', post)

    bindGet('#getproducts', '/catalog/products')

    bindGet('#getbasket', '/shopping/basket')
    bindSend('#putbasket', '/shopping/basket/put', put)
    bindSend('#removebasket', '/shopping/basket/remove', del)


    bindGet('#getstock', '/stock/stock')
    bindSend('#newstockitem', '/stock/newstockitem', post)

});