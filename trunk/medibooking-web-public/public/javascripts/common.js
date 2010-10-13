/**
 * On load the page in case of errors, focus on the first field with errors
 */
var FormUtils = {
	focusFirstErrorField : function() {
		// find all elements with class=hasError
		hasErrorElement = $('.hasError').find('input');

		if (hasErrorElement) {

			hasErrorElement.focus();

		}
	}
};

var FloatingMessagesUtils = {
		close : function() {
			$('#floating-message-wrapper').hide();
			return false;
		}
};

$(window).scroll(function() {
	
	$('#floating-message-wrapper').animate({
		top : $(window).scrollTop() + "px"
	}, { "duration": 100, "easing": "easeInCubic"});
	
	
});
