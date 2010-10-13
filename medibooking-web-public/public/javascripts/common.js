/**
 * On load the page in case of errors, focus on the first field with errors
 */
var FormUtils = {
	focusFirstErrorField : function() {
		// find all elements with class=hasError		
		hasErrorElements = $('.hasError').eq(0).find('input').focus();
	}
};

var FloatingMessagesUtils = {
	close : function() {
		$('#floating-message-wrapper').hide();
		return false;
	},
	
	autoClose : function() {
		
	}
};

//Add common windo onLoad events
$(window).load(function() {

	if ( $('#floating-message-wrapper') ) {
		
		var t = setTimeout ( "$('#floating-message-wrapper').fadeOut()", 10000);
		
		
	}
});

$(window).scroll(function() {
	if ($('#floating-message-wrapper')) {
		$('#floating-message-wrapper').animate({
			top : $(window).scrollTop() + "px"
		}, {
			"duration" : 100,
			"easing" : "easeInCubic"
		});
	}

});
