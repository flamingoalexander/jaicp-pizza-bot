function deleteFromCart(name){
    var $session = $jsapi.context().session;
    for(var i = 0; i < $session.cart.length; i++){
        var current_position = $session.cart[i];
        if(current_position.name === name){
            $session.cart.splice(i, 1);
        }
    }
    log('!!!!!! CART: ' + JSON.stringify($session.cart));
}

function getTotalSum(){
    var totalSum = 0;
    var $session = $jsapi.context().session;

    for(var i = 0; i < $session.cart.length; i++){
        var current_position = $session.cart[i];
        for(var id = 1; id < Object.keys(pizza).length + 1; id++){
            if (current_position.name === pizza[id].value.title){
                var variation = _.find(pizza[id].value.variations, function(variation){
                    return variation.id === current_position.id;
                });
                totalSum += variation.price * current_position.quantity;
            }
        }
    }
    log("!!!!!!!!!!!! totalSum = " + totalSum);
    return totalSum;
}

function editText(messageId, text) {
    var $response = $jsapi.context().response;
    var reply = {
        type: "raw",
        body: {
            text: text,
            message_id: messageId,
            reply_markup: {
                "resize_keyboard": false,
            }
        },
        method: "editMessageText"
    };

    $response.replies = $response.replies || [];
    $response.replies.push(reply);
}