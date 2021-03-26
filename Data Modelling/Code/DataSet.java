import java.util.ArrayList;


public  class DataSet {

    private String _industryName, _industryNameLabel;
    private ArrayList<DataItem> _dataItems = new ArrayList<DataItem>();

    public DataSet() {

    }

    public DataSet(String industry) {
        _industryName = industry;
    }

    public void setIndustryName(String industryName) {
        _industryName = industryName;
    }

    public String getIndustryName() {
        return _industryName;
    }

    public String getIndustryNameLabel() {
        _industryNameLabel = _industryName;
        String[] industryNameWithSpaces = _industryName.split("\\s+");
        if(industryNameWithSpaces.length > 0) {
            String industryNameWithSpacesNew = new String();
            for (int y = 0 ; y < industryNameWithSpaces.length ; y++)  {
                if(industryNameWithSpaces[y].charAt(industryNameWithSpaces[y].length()-1)==(',')
                        ||industryNameWithSpaces[y].charAt(industryNameWithSpaces[y].length()-1)==(';'))
                {
                    industryNameWithSpaces[y] = industryNameWithSpaces[y].substring(0, industryNameWithSpaces[y].length() - 1);
                }
                if(industryNameWithSpaces[y].equals("<")) {
                    industryNameWithSpaces[y] = "LessThan";
                }
                if(industryNameWithSpaces[y].equals("+")) {
                    industryNameWithSpaces[y] = "Plus";
                }

                industryNameWithSpacesNew += industryNameWithSpaces[y];
            }
            _industryNameLabel = industryNameWithSpacesNew ;
        }

        return _industryNameLabel;
    }

    public void addDataItem(DataItem dataItem) {
        _dataItems.add(dataItem);
    }

    public ArrayList<DataItem> getDataItems() {
        return _dataItems;
    }

    public String toString(int n) {
        String dataSetStr = new String();
        for(int i = 0; i < _dataItems.size(); i++) {
            dataSetStr += _dataItems.get(i).toString(n) + "\t qb:dimension :" + getIndustryNameLabel() + ";  \n" ;
            dataSetStr += "\t qb:dimension :TP2020_04. \n \n";
        }
        return dataSetStr;
    }


}
