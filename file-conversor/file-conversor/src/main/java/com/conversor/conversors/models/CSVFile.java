package com.conversor.conversors.models;

import com.conversor.abstracts.DataFile;
import com.conversor.abstracts.FileType;
import com.conversor.abstracts.IFileConversor;
import com.conversor.conversors.FileConversor;

public class CSVFile extends FileConversor implements IFileConversor {

    public CSVFile(String fileName, String content) {
        super(fileName, content, FileType.CSV);
    }

    @Override
    public CSVFile convertToCSV() {
        return this;
    }

    @Override
    public HTMLFile convertToHTML() { 
        String string = "<html>\n\n\t<body>\n\t\t<table>\n";
        String[] lines = this.getContent().split(System.lineSeparator()); //separa o conteudo do arquivo CSV por linhas
        String[] columns = getValues(lines[0]); //pega os valores da primeira linha - o nome das colunas
        for(int i = 0; i < lines.length; i++){
            if(i == 0){ //passa o nome das colunas para HTML - "bloco" com <th>
                string += "\t\t\t<tr>\n";
                for(int j = 0; j < columns.length; j++){ //contagem do numero de colunas
                    string += "\t\t\t\t<th>" + columns[j] + "</th>\n";  
                }
                string += "\t\t\t</tr>\n";
            }
            else{ //passa os valores das colunas para HTML - "blocos" com <td>
                String[] values = getValues(lines[i]); //pega os valores de cada linha 
                string += "\t\t\t<tr>\n";
                for(int k = 0; k < values.length; k++){ //contagem do numero de colunas
                    string += "\t\t\t\t<td>" + values[k] + "</td>\n"; 
                }
                string += "\t\t\t</tr>\n";
            }  
        }
        string += "\t\t</table>\n\t</body>\n\n</html>";
        
        return new HTMLFile(this.getFileName(), string);
    }

    @Override
    public XMLFile convertToXML() { 
        String string = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n";
        String[] lines = this.getContent().split(System.lineSeparator()); //separa o conteudo do arquivo CSV por linhas
        String[] columns = getValues(lines[0]); //pega os valores da primeira linha - o nome das colunas
        for(int i = 1; i < lines.length; i++){  //i = 1, pois os valores de lines[0] já foram armazenados em columns
            String[] values = getValues(lines[i]); //pega os valores de cada linha do arquivo CSV, exceto os da primeira linha (lines[0]), já que esses já foram armazenados em columns
            string += "\t<row>\n";
            for(int j = 0; j < columns.length; j++){ //passa os nomes das colunas e seus valores para XML
                string += "\t\t<" + toCamelCase(columns[j]) + ">" + values[j] + "</" + toCamelCase(columns[j]) + ">\n";  
            }
            string += "\t</row>\n";
        }
        string += "</root>";

        return new XMLFile(this.getFileName(), string);
    }

    @Override
    public DataFile convertToFormat(FileType fileType) { 
        if(fileType == FileType.HTML){
            return this.convertToHTML();
        }
        else if(fileType == FileType.XML){
            return this.convertToXML();
        }
        return null;
    }

    /*
     * Método auxiliar para ler os conteúdos uma linha de um arquivo CSV
     * Utiliza um regex para separar os campos da linha
     * Exemplo: "1,2,3,4,5" -> ["1", "2", "3", "4", "5"]
     * @param line: String
     * @return String[]
     */
    private String[] getValues(String line) {
        return line.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
    }
}
