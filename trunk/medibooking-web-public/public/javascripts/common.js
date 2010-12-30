//Custom KO bindings

ko.bindingHandlers.id = {
	init : function(element, valueAccessor, allBindingsAccessor, viewModel) {
		element.id = valueAccessor();
		// This will be called when the binding is first applied to an element
		// Set up any initial state, event handlers, etc. here
	},
	update : function(element, valueAccessor, allBindingsAccessor, viewModel) {
		// This will be called once when the binding is first applied to an
		// element,
		// and again whenever the associated observable changes value.
		// Update the DOM element based on the supplied values here.
		element.id = valueAccessor();
	}
};

ko.bindingHandlers.onClick = {
		init : function(element, valueAccessor, allBindingsAccessor, viewModel) {
			$(element).click(function() {
				eval(valueAccessor());
			})
			// This will be called when the binding is first applied to an element
			// Set up any initial state, event handlers, etc. here
		},
		update : function(element, valueAccessor, allBindingsAccessor, viewModel) {
			// This will be called once when the binding is first applied to an
			// element,
			// and again whenever the associated observable changes value.
			// Update the DOM element based on the supplied values here.
			element.id = valueAccessor();
		}
	};

var responseBus = {

	handle : function(data) {
		formUtils.clearErrors();
		// check if data is JSON

		if (data.error) {
			jsUtils.showResultStatus(data.error, jsUtils.messageType.error);
			if (data.errors) {
				formUtils.showErrors(data.errors);
			}
		} else if (data.success) {
			jsUtils.showResultStatus(data.success, jsUtils.messageType.success);
		} else if (data.warning) {
			jsUtils.showResultStatus(data.warning, jsUtils.messageType.warning);
		}

		// Check if should redirect
		if (data.redirectTo) {
			window.location = data.redirectTo;

			// window.alert("Redirect to: " +data.redirectTo);
		}

		// Check for registered listeners
	}
};

var formUtils = {
	clearErrors : function() {
		// find any element with class fieldError and set html to ""
		// find any element with class hasError and remove if
		$(".fieldError").html("");
		$(".hasError").removeClass("hasError");

	},
	showErrors : function(errors) {
		$.each(errors, function(k, v) {
			var id = '#' + k.replace(/\./g, '\\.');
			if ($(id)) {

				$(id).addClass("hasError");
				var errorField = id + "\\.error";
				var errorText = ""
				for ( var i = 0; i < v.length; i++) {
					errorText += "<div>" + v[i] + "</div>";
				}
				$(errorField).html(errorText);
				$(".hasError:first").focus();
			}
		})
	}
};

var jsUtils = {

	startWorkingStatus : function() {
		$('#page-main-wrapper').css({
			'opacity' : 0.5
		});
		// see http://jquery.malsup.com/block/
		$
				.blockUI({
					message : '<p><img src="/public/images/busy.gif" /></p><p>Por favor aguarde...</p>'
				});
	},

	endWorkingStatus : function() {
		$('#page-main-wrapper').css({
			'opacity' : 1
		});
		// see http://jquery.malsup.com/block/
		$.unblockUI();
	},

	showResultStatus : function(message, type) {

		// check if any exists already
		if ($('#floating-message-wrapper')) {
			$('#floating-message-wrapper').remove();
		}

		var content = '<div id="floating-message-wrapper" class="' + type
				+ '">';
		content += '<div id="page-message"><strong>' + message + '</strong>';
		content += '</div><div id="page-message-close"><a href="javascript:void(0);" onClick="floatingMessagesUtils.close();">X</a></div></div>';
		$('#page-main-wrapper').prepend(content);
		$('#floating-message-wrapper').css({
			'top' : $(window).scrollTop() + 'px'
		});
		$('#floating-message-wrapper').show();
		floatingMessagesUtils.scroll();
		floatingMessagesUtils.autoClose();

	},

	messageType : {
		error : "error",
		warning : "warning",
		success : "success"
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
