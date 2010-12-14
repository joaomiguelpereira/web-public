var jsUtils = {
	
	startWorkingStatus: function() {
		$('#page-main-wrapper').css({ 'opacity' : 0.5 });
		//see http://jquery.malsup.com/block/
		$.blockUI();	
	},
	
	endWorkingStatus: function() {
		$('#page-main-wrapper').css({ 'opacity' : 1 });
		//see http://jquery.malsup.com/block/
		$.unblockUI();
	},
	
	
	showResultStatus: function(message, type) {
		
		
			
		var content = '<div id="floating-message-wrapper" class="'+type+'">';
		content += '<div id="page-message"><strong>'+message+'</strong>';
		content +='</div><div id="page-message-close"><a href="javascript:void(0);" onClick="floatingMessagesUtils.close();">X</a></div></div>';
		$('#page-main-wrapper').prepend(content);
		$('#floating-message-wrapper').css({'top': $(window).scrollTop() + 'px'});
		$('#floating-message-wrapper').show();
		floatingMessagesUtils.scroll();
		floatingMessagesUtils.autoClose();
		
		
	 
		
	},
	
	messageType : { 
		error: "error",
		warning: "warning",
		success: "success"
	}
	
};



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


var floatingMessagesUtils = {
	close : function() {
		$('#floating-message-wrapper').hide();
		return false;
		
	},

	autoClose : function() {
		var t = setTimeout("$('#floating-message-wrapper').fadeOut()", 10000);
	},
	scroll : function() {
		if ($('#floating-message-wrapper')) {
			$('#floating-message-wrapper').animate({
				top : $(window).scrollTop() + "px"
			}, {
				"duration" : 100,
				"easing" : "easeInCubic"
			});
		}
	} 
};

// Add common window onLoad events
$(window).load(function() {

	if ($('#floating-message-wrapper')) {
		floatingMessagesUtils.autoClose();
	}
	
});

$(window).scroll(function() {
	floatingMessagesUtils.scroll();
});
