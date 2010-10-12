
/**
 * Register event handler for type of business. Do it when page is loaded
 */



$(window).load(function () {
	//In case of reload from validation errors, select the correct type and show the form
	
	if ( $("input[name=partner.businessType]").val() != "" ) {
		var type = $("input[name=partner.businessType]").val();
		if ( $("#"+type) ) {
			$("#"+type).attr('checked', true );
			updateForm(type);
		}
	}
	
	//Add event listener from change events
	$("input[name=businessTypeChoice]").change( function() { 
		updateForm($("input[name=businessTypeChoice]:checked").val());
		
	});
	
});


/**
 * On load the page in case of erros, focus on the first field with errors
 */
function focusFirstErrorField() {
	
}

/**
 * Show and update the form
 * @param type the Business typr
 */
function updateForm(type) {
	//show the form
	$('#preRegisterPartnerForm').show();
	//change occurences
	$('.businessTypeWithPreposition').html(names[type][2]);
	$('.businessType').html(names[type][0]);
	$('.businessTypeWithPronoum').html(names[type][1]);
	$("input[name=partner.businessType]").val(type);
	window.alert($("input[name=partner.businessType]").val());
	
	
}