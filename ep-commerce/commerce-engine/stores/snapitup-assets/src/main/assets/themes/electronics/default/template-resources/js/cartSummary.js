function updateCartSummary (shoppingCart) {
	if (shoppingCart.inclusiveTaxCalculationInUse == false
		&& shoppingCart.subtotalDiscountMoney != null && shoppingCart.subtotalDiscountMoney.amount > 0) {
		document.getElementById("promotion-exclusive").style.display="";

		formatMoney(shoppingCart.subtotalDiscountMoney, shoppingCart.locale, "exclusive-discount-value");
	} else {
		document.getElementById("promotion-exclusive").style.display="none";
	}

	formatMoney(shoppingCart.subtotalMoney, shoppingCart.locale, "subTotalValue");
	
	document.getElementById("shipping").style.display="";

	formatMoney(shoppingCart.shippingCost, shoppingCart.locale, "cartShippingCostValue");
	
	var cartSummaryTable = document.getElementById("cart-summary-table");
	var rows = cartSummaryTable.getElementsByTagName("tr");
	var taxRows = new Array();
	for (var i = 0; i < rows.length; i++){
		if(rows[i].id && rows[i].id.match(/tax\d+/)) {
			rows[i].parentNode.deleteRow(i);
			i--;
		}
	}
	var hasTax = false;
	var naTaxNode = document.getElementById("tax-na");
	var count = 1;
	if (shoppingCart.localizedTaxMap) {
		for (var taxCategoryName in shoppingCart.localizedTaxMap) {
			hasTax = true;
			var newRow = cartSummaryTable.tBodies[0].insertRow(naTaxNode.sectionRowIndex);
			newRow.className = "tax";
			newRow.id = "tax" + count;
			count++;

			var tcTD = newRow.insertCell(0);
			tcTD.setAttribute("class", "title");
			tcTD.appendChild(document.createTextNode(taxCategoryName + ":"));

			var valueTD = newRow.insertCell(1);
			valueTD.setAttribute("class", "value");
			valueTD.setAttribute("id", taxCategoryName + i);
			valueTD.appendChild(document.createTextNode(''));
			formatMoney(shoppingCart.localizedTaxMap[taxCategoryName], shoppingCart.locale, taxCategoryName + i);
		}
	}
	if (hasTax) {
		document.getElementById("tax-na").style.display="none";
	} else {
		document.getElementById("tax-na").style.display="";
	}

	if (shoppingCart.inclusiveTaxCalculationInUse == true
		&& shoppingCart.subtotalDiscountMoney != null && shoppingCart.subtotalDiscountMoney.amount > 0)  {
		document.getElementById("promotion-inclusive").style.display="";
		formatMoney(shoppingCart.subtotalDiscountMoney, shoppingCart.locale, "inclusive-discount-value");
	} else {
		document.getElementById("promotion-inclusive").style.display="none";
	}

	var giftCertificateRedeemDiv=document.getElementById("gift-certificate-value");
	if (giftCertificateRedeemDiv) {
		formatMoney(shoppingCart.giftCertificateDiscountMoney, shoppingCart.locale, "gift-certificate-value");
	}

	formatMoney(shoppingCart.beforeTaxTotal, shoppingCart.locale, "totalBeforeTaxValue");
	formatMoney(shoppingCart.totalMoney, shoppingCart.locale, "cartTotalValue");
}
