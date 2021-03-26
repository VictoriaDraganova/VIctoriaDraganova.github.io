public  class DataItem {

    private String _itemTitle, _itemValue, _itemLabel, _cellNumber ;

    public DataItem() {

    }

    public DataItem(String itemName, String itemValue, String cellNumber) {
        _itemTitle = itemName;
        _itemValue = itemValue;
        _cellNumber = cellNumber;
    }

    public String getItemTitle() {
        return _itemTitle;
    }

    public String getItemValue() {
        return _itemValue;
    }

    public String getItemLabel() {
        _itemLabel = _itemTitle;
        String[] numberOfServeysSent = _itemTitle.split("\\s+");
        if(numberOfServeysSent.length > 0) {
            String offenceTypeNew = new String();
            for (int y = 0 ; y < numberOfServeysSent.length ; y++)  {

                if(numberOfServeysSent[y].equals("<")) {
                    numberOfServeysSent[y] = "LessThan";
                }
                if(numberOfServeysSent[y].equals("+")) {
                    numberOfServeysSent[y] = "Plus";
                }
                offenceTypeNew += numberOfServeysSent[y];
            }
            _itemLabel = offenceTypeNew ;
        }
        return _itemLabel;
    }

    public String getCellNumber() {
        return _cellNumber;
    }

    public String toString(int n) {
        getItemLabel();
        return ":ds"+ n +"_" + getCellNumber() + " rdf:type qb:Observation; \n" +
                "\t rdf:value " + _itemValue + "; \n" +
                "\t qb:dataset :ds"+ n +"; \n" +
                "\t qb:dimension :" + _itemLabel + "; \n"
                ;
    }
}
