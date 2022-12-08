package com.conversor.conversors.models;

import java.util.ArrayList;
import java.util.List;

import com.conversor.abstracts.DataFile;
import com.conversor.abstracts.FileType;
import com.conversor.abstracts.IFileConversor;
import com.conversor.conversors.FileConversor;

public class XMLFile extends FileConversor implements IFileConversor {

    public XMLFile(String fileName, String content) {
        super(fileName, content, FileType.XML);
    }

    @Override
    public CSVFile convertToCSV() {
        String string = "";
        String[] lines = this.getContent().split("<row>"); //separa o conteudo do arquivo XML a cada "<row>"
        List<String> rows = getRows(lines); //chama o metodo getRows

        List<String> columnNames = getColumnNames(rows); //chama o metodo getColumnNames e armazena os nomes das colunas
        for(int i = 0; i < columnNames.size()-1; i++){
            string += columnNames.get(i) + ","; //passa o nomes das colunas para a primeira linha do CSV 
        }
        string += "\n";

        List<String> values = getValues(rows); //chama o metodo getValues e armazena os valores das colunas
        int count = 1; //contador do numero de colunas
        for(int i = 0; i < values.size(); i++){
            string += values.get(i) + ","; //passa cada valor para HTML
            if(count == (columnNames.size() - 1)){ //compara o count com a quantidade de colunas total -> se for igual, troca de linha e reinicia a contagem
                count = 0;
                string += "\n";
            }
            count++; 
        }

        return new CSVFile(this.getFileName(), string);
    }

    @Override
    public HTMLFile convertToHTML() { 
        String string = "<html>\n\n\t<body>\n\t\t<table>\n";
        
        String[] lines = this.getContent().split("<row>"); //separa o conteudo do arquivo XML a cada "<row>"
        List<String> rows = getRows(lines); //chama o metodo getRows
        
        List<String> columnNames = getColumnNames(rows); //chama o metodo getColumnNames e armazena os nomes das colunas
        string += "\t\t\t<tr>\n";
        for(int i = 0; i < columnNames.size()-1; i++){   
            string += "\t\t\t\t<th>" + columnNames.get(i) + "</th>\n"; //passa o nomes das colunas para o primeiro "bloco" (com a tag <th>) do HTML 
        }
        string += "\t\t\t</tr>\n";

        List<String> values = getValues(rows); //chama o metodo getValues e armazena os valores das colunas
        int k = 0; //contador do indice de values, ou seja, para acessar cada elemento da lista values
        for(int i = 0; i < rows.size(); i++){ //loop a cada <tr>
            string += "\t\t\t<tr>\n";
            for(int j = 0; j < columnNames.size()-1; j++){ //contagem do numero de colunas
                string += "\t\t\t\t<td>" + values.get(k) + "</td>\n"; //passa cada valor para HTML ("bloco" com <td>)
                k++; 
            }
            string += "\t\t\t</tr>\n";
        } 
        string += "\t\t</table>\n\t</body>\n\n</html>";
        
        return new HTMLFile(this.getFileName(), string);
    }

    @Override
    public XMLFile convertToXML() {
        return this;
    }

    @Override
    public DataFile convertToFormat(FileType fileType) {
        if(fileType == FileType.HTML){
            return this.convertToHTML();
        }
        else if(fileType == FileType.CSV){
            return this.convertToCSV();
        }
        return null;
    }

    /*
     * Método auxiliar para identificar as linhas de um documento XML,
     * separadas pela tag <row>
     * @param lines: String[]
     * @return List<String>: Linhas do documento
     */
    private List<String> getRows(String[] lines) {
        List<String> rows = new ArrayList<String>();
        for (int i = 1; i < lines.length; i++) {
            lines[i] = lines[i].strip();
            if (!lines[i].isBlank() && lines[i].contains("</row>"))
                rows.add(lines[i].replace("</row>", "").strip());
        }
        return rows;
    }

    /*
     * Método auxiliar para identificar as colunas de um documento XML
     * Converte o texto de dentro das tags XML para Title Case, e adiciona
     * na lista de colunas
     * @param rows: List<String>
     * @return List<String>: Lista de colunas
     */
    private List<String> getColumnNames(List<String> rows) {
        List<String> columnNames = new ArrayList<String>();
        for (String row : rows) {
            String[] columns = row.replace("/", "").split("<");
            for (String column : columns) {
                if (column.contains(">")) {
                    column = toTitleCase(column.substring(0, column.indexOf(">")));
                    if (!columnNames.contains(column)) {
                        columnNames.add(column);
                    }
                }
            }
        }
        return columnNames;
    }

    /*
     * Método auxiliar para identificar os valores das colunas de um documento XML,
     * que ficam entre as tags <nomeColuna></nomeColuna>
     * @param rows: List<String>
     * @return List<String>: Lista de valores
     */
    private List<String> getValues(List<String> rows) {
        List<String> values = new ArrayList<String>();
        for (String row : rows) {
            String[] columns = row.split(">");
            for (String column : columns) {
                column = column.substring(0, column.indexOf("<")).strip();
                if (!column.isBlank())
                    values.add(column);
            }
        }
        return values;
    }
}
