$(document).ready(function(){

    var tryRedirect = function(redirectInfo){
        try {
            var redirectJson = JSON.parse(redirectInfo)
            window.location.replace(redirectJson.redirect)
        } catch(e) {}
    }

    $('#logout').click(function(){
        var nonce = 'NONCE'
        $.ajax({
                url: '/logout',
                type: 'POST',
                username: nonce,
                password: nonce,
                success: function () { alert('hmm. expected 401 here.'); },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status == 401)
                        tryRedirect(jqXHR.responseText)
                }
        })
    })

    $.ajax({
        url: '/api/user/currentuser'
    }).then(function(userinfo){
        $('#greeting').text('Hello ' + userinfo.content + '!')
    })

});