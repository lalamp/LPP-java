package com.conversor.conversors.models;

import java.util.ArrayList;
import java.util.List;

import com.conversor.abstracts.DataFile;
import com.conversor.abstracts.FileType;
import com.conversor.abstracts.IFileConversor;
import com.conversor.conversors.FileConversor;

public class HTMLFile extends FileConversor implements IFileConversor {

    public HTMLFile(String fileName, String content) {
        super(fileName, content, FileType.HTML);
    }

    @Override
    public CSVFile convertToCSV(){
        String string = "";

        List<String> columnNames = new ArrayList<String>();
        String[] rows = this.getContent().split("<tr>"); //separa o conteudo do HTML por <tr> 
        String[] columns = rows[1].replace("</tr>", "").strip().split("<th>"); //substitui "</tr>" por "", retira os espacos vazios e separa, por <th>, o conteudo do bloco que contem os nomes das colunas
        for(int i = 0; i < columns.length; i++){
            if(columns[i].strip() != ""){ //ignora todos valores que sao ""
                columnNames.add(columns[i].substring(0, columns[i].indexOf("<"))); //adiciona os nomes das colunas a lista columnNames
            }
        }
        for(int i = 0; i < columnNames.size(); i++){ //passa o nomes das colunas para a primeira linha do CSV
            string += columnNames.get(i) + ",";
        }
        string += "\n";

        List<String> values = new ArrayList<String>(); 
        for(int i = 2; i < rows.length; i++){ //comeca com i = 2, pois nao queremos o rows[0] (com as tags <html>, <body>...), nem o rows[1] (com os nomes das colunas)
            String[] value = rows[i].replace("</tr>", "").strip().split("<td>"); //substitui "</tr>" por "", retira os espacos vazios e separa, por <td>, o conteudo do bloco que contem os valores das colunas
            for(int j = 0; j < value.length; j++){
                if(value[j].strip() != ""){ //ignora todos valores que sao ""
                    values.add(value[j].substring(0, value[j].indexOf("<"))); //adiciona os valores da rows[i] a lista values
                }
            }
            for(int k = 0; k < values.size(); k++){
                string += values.get(k) + ","; //passa os valores para uma linha do arquivo CSV
            }
            string += "\n"; //passa para a proxima linha
            values.clear(); //esvazia a lista values para armazenar os valores da rows seguinte
        }

        return new CSVFile(this.getFileName(), string);
    }

    @Override
    public HTMLFile convertToHTML() {
        return this;
    }

    @Override
    public XMLFile convertToXML() {
        String string = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n";

        List<String> columnNames = new ArrayList<String>();
        String[] rows = this.getContent().split("<tr>"); //separa o conteudo do HTML por <tr> 
        String[] columns = rows[1].replace("</tr>", "").strip().split("<th>"); //substitui "</tr>" por "", retira os espacos vazios e separa, por <th>, o conteudo do bloco que contem os nomes das colunas 
        for(int i = 0; i < columns.length; i++){
            if(columns[i].strip() != ""){ //ignora todos valores que sao ""
                columnNames.add(columns[i].substring(0, columns[i].indexOf("<"))); //adiciona os nomes das colunas a lista columnNames
            }
        }
        List<String> values = new ArrayList<String>(); 
        for(int i = 2; i < rows.length; i++){ //comeca com i = 2, pois nao queremos o rows[0] (com as tags <html>, <body>...), nem o rows[1] (com os nomes das colunas)
            String[] value = rows[i].replace("</tr>", "").strip().split("<td>"); //substitui "</tr>" por "", retira os espacos vazios e separa, por <td>, o conteudo do bloco que contem os valores das colunas 
            for(int j = 0; j < value.length; j++){
                if(value[j].strip() != ""){ //ignora todos valores que sao ""
                    values.add(value[j].substring(0, value[j].indexOf("<"))); //adiciona os valores da rows[i] a lista values
                }
            }
            string += "\t<row>\n";
            for(int k = 0; k < values.size(); k++){
                string +=  "\t\t<" + columnNames.get(k) + ">" + values.get(k) + "<" + columnNames.get(k) + ">\n"; //passa os nomes e valores das colunas para XML
            }
            string += "\t</row>\n";
            values.clear(); //esvazia a lista values para armazenar os valores da rows seguinte
        }
    
        string += "</root>";

        return new XMLFile(this.getFileName(), string); 
    }

    @Override
    public DataFile convertToFormat(FileType fileType) {
        if(fileType == FileType.XML){
            return this.convertToXML();
        }
        else if(fileType == FileType.CSV){
            return this.convertToCSV();
        }
        return null;
    }
}
