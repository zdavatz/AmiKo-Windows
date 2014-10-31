package com.maxl.java.amikodesk;

public class Article {
	private String pack_title;
	private String pack_size;
	private String pack_unit;
	private String exfactory_price;
	private String public_price;
	private String additional_info;
	private String ean_code;
	private String pharma_code;
	private float buying_price = 0.0f;
	private float selling_price = 0.0f;;
	private int quantity = 1;
	private int draufgabe = 0;
	private int onstock;
	private boolean special = false;
	
	public Article(String[] entry) {
		if (entry!=null) {
			if (Utilities.appLanguage().equals("de")) {
				ean_code = pharma_code = pack_title = pack_size 
					= pack_unit = public_price = exfactory_price = additional_info = "k.A.";
			} else if (Utilities.appLanguage().equals("fr")) {
				ean_code = pharma_code = pack_title = pack_size 
						= pack_unit = public_price = exfactory_price = additional_info = "p.c.";
			}
			if (entry.length>7) {
				if (!entry[0].isEmpty())
					pack_title = entry[0];
				if (!entry[1].isEmpty())
					pack_size = entry[1];
				if (!entry[2].isEmpty())
					pack_unit = entry[2];
				if (!entry[3].isEmpty())
					public_price = entry[3];
				if (!entry[4].isEmpty())
					exfactory_price = entry[4];
				if (!entry[5].isEmpty())				
					additional_info = entry[5];				
				if (!entry[6].isEmpty())
					ean_code = entry[6];
				if (!entry[7].isEmpty())
					pharma_code = entry[7];
			}
			quantity = 1;
		}
	}
	
	public String getEanCode() {
		return ean_code;
	}
	
	public void setEanCode(String ean_code) {
		this.ean_code = ean_code;
	}
	
	public String getPharmaCode() {
		return pharma_code;
	}

	public void setPharmaCode(String pharma_code) {
		this.pharma_code = pharma_code;
	}
	
	public String getPackTitle() {
		return pack_title;
	}

	public void setPackTitle(String pack_title) {
		this.pack_title = pack_title;
	}
	
	public String getPackSize() {
		return pack_size;
	}

	public void setPackSize(String pack_size) {
		this.pack_size = pack_size;
	}
	
	public String getPackUnit() {
		return pack_unit;
	}

	public void setPackUnit(String pack_unit) {
		this.pack_unit = pack_unit;
	}
	
	public boolean isSpecial() {
		return special;
	}
	
	public void setSpecial(boolean special) {
		this.special = special;
	}
	
	/**
	 * Exfactory price
	*/
	public String getExfactoryPrice() {
		return exfactory_price;
	}

	public void setExfactoryPrice(String exfactory_price) {
		this.exfactory_price = exfactory_price;
	}
	
	public String getCleanExfactoryPrice() {
		String price = exfactory_price;
		String price_pruned = price.replaceAll("[^\\d.]", "");
		if (!price_pruned.isEmpty() && !price_pruned.equals(".."))
			price = price_pruned;
		else
			price = "k.A.";
		return price;
	}
	
	public float getExfactoryPriceAsFloat() {
		float exfacto_as_float = 0.0f;
		String price_pruned = exfactory_price.replaceAll("[^\\d.]", "");
		if (!price_pruned.isEmpty() && !price_pruned.equals("..")) {
			exfacto_as_float = Float.parseFloat(price_pruned);
		}						
		return exfacto_as_float;
	}

	public float getTotExfactoryPrice() {
		return quantity*getExfactoryPriceAsFloat();
	}
	
	/**
	 * Public price
	*/	
	public String getPublicPrice() {
		return public_price;
	}	
	
	public void setPublicPrice(String public_price) {
		this.public_price = public_price;
	}	
	
	public String getCleanPublicPrice() {
		String price = public_price;
		String price_pruned = price.replaceAll("[^\\d.]", "");
		if (!price_pruned.isEmpty() && !price_pruned.equals(".."))
			price = price_pruned;
		else
			price = "k.A.";
		return price;
	}
	
	public float getPublicPriceAsFloat() {
		float public_as_float = 0.0f;
		String price_pruned = public_price.replaceAll("[^\\d.]", "");
		if (!price_pruned.isEmpty() && !price_pruned.equals("..")) {
			public_as_float = Float.parseFloat(price_pruned);
		}						
		return public_as_float;	
	}
	
	public float getTotPublicPrice() {
		return quantity*getPublicPriceAsFloat();
	}
	
	/**
	 * Buying price (what the doctor/pharmacy pays, defined by drug company)
	*/	
	public float getBuyingPrice() {					
		return buying_price;
	}
	
	public float getTotBuyingPrice() {
		return quantity*buying_price;
	}

	public void setBuyingPrice(float buying_price) {
		this.buying_price = buying_price;
	}
	
	/**
	 * Buying price = consumer price (what the consumer pays, defined by doctor/pharmacy)
	*/		
	public float getSellingPrice() { 	
    	return selling_price;
	}

	public float getTotSellingPrice() {
		return (quantity+draufgabe)*selling_price;
	}
	
	public void setSellingPrice(float buying_price, float margin) {
		this.selling_price = (1.0f+margin)*buying_price;;
	}
	
	public String getAdditionalInfo() {
		return additional_info;
	}	
	
	public void setAdditionalInfo(String additional_info) {
		this.additional_info = additional_info;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void incrementQuantity() {
		quantity++;
	}
	
	public void decrementQuantity() {
		quantity--;
		if (quantity<0)
			quantity = 0;
	}
	
	public int getDraufgabe() {
		return draufgabe;
	}
	
	public void setDraufgabe(int draufgabe) {
		this.draufgabe = draufgabe;
	}
	
	public void setItemsOnStock(int onstock) {
		this.onstock = onstock;
	}
	
	public int getItemsOnStock() {
		return onstock;
	}
}
