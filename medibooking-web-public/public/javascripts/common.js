/**
 * On load the page in case of errors, focus on the first field with errors
 */
var FormUtils = {
	focusFirstErrorField : function() {
		// Try first as input
		$('.hasError').eq(0).find('input').focus();
		$('.hasError').eq(0).find('textarea').focus();
		
	}
};

var FloatingMessagesUtils = {
	close : function() {
		$('#floating-message-wrapper').hide();
		return false;
	},

	autoClose : function() {
		var t = setTimeout("$('#floating-message-wrapper').fadeOut()", 10000);
	}
};

// Add common windo onLoad events
$(window).load(function() {

	if ($('#floating-message-wrapper')) {
		FloatingMessagesUtils.autoClose();
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
