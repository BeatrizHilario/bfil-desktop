package autoeletrica.util;

import autoeletrica.model.Cliente;
import autoeletrica.model.EmpresaConfig;
import autoeletrica.model.ServicoConsultaDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

public class GeradorOrdemServicoPDF {

    public static File gerarOrdemServico(EmpresaConfig emp, Cliente cliente, ServicoConsultaDTO servico) throws Exception {
        File pdfFile = File.createTempFile("OS_" + (servico.getId() != null ? servico.getId() : "NOVA") + "_", ".pdf");

        Document doc = new Document(PageSize.A4, 25, 25, 25, 25);
        PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
        doc.open();

        Font fHeaderTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fHeaderSub = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font fSecTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font fVal = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fSmall = FontFactory.getFont(FontFactory.HELVETICA, 7);

        // 1. CABEÇALHO DA EMPRESA
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{65, 35});

        PdfPCell cEmpresa = new PdfPCell();
        cEmpresa.setBorder(Rectangle.NO_BORDER);
        cEmpresa.addElement(new Paragraph(emp.getNomeFantasia() != null ? emp.getNomeFantasia() : "AUTO ELÉTRICA", fHeaderTitle));
        cEmpresa.addElement(new Paragraph((emp.getLogradouro() != null ? emp.getLogradouro() : "") + ", " + (emp.getNumero() != null ? emp.getNumero() : "") + " - " + (emp.getBairro() != null ? emp.getBairro() : ""), fHeaderSub));
        cEmpresa.addElement(new Paragraph((emp.getCidade() != null ? emp.getCidade() : "") + " (" + (emp.getUf() != null ? emp.getUf() : "") + ") - CEP: " + (emp.getCep() != null ? emp.getCep() : ""), fHeaderSub));
        cEmpresa.addElement(new Paragraph("CNPJ: " + (emp.getCnpj() != null ? emp.getCnpj() : "-") + "   IE: " + (emp.getInscricaoEstadual() != null ? emp.getInscricaoEstadual() : "-"), fHeaderSub));
        cEmpresa.addElement(new Paragraph("Fone: " + (emp.getTelefone() != null ? emp.getTelefone() : "-") + " | " + (emp.getEmail() != null ? emp.getEmail() : ""), fHeaderSub));
        headerTable.addCell(cEmpresa);

        PdfPCell cOsInfo = new PdfPCell();
        cOsInfo.setBorder(Rectangle.BOX);
        cOsInfo.setPadding(8);
        Paragraph pOsNum = new Paragraph("ORDEM DE SERVIÇO\nNº " + (servico.getId() != null ? String.format("%04d", servico.getId()) : "NOVA"), fHeaderTitle);
        pOsNum.setAlignment(Element.ALIGN_CENTER);
        cOsInfo.addElement(pOsNum);
        Paragraph pData = new Paragraph("\nData de Entrada: " + servico.getData(), fHeaderSub);
        pData.setAlignment(Element.ALIGN_CENTER);
        cOsInfo.addElement(pData);
        headerTable.addCell(cOsInfo);

        doc.add(headerTable);
        doc.add(new Paragraph(" "));

        // 2. DADOS DO CLIENTE
        PdfPTable tblCliente = new PdfPTable(4);
        tblCliente.setWidthPercentage(100);
        tblCliente.setWidths(new float[]{15, 45, 15, 25});

        addHeaderCell(tblCliente, "DADOS DO CLIENTE", 4, fSecTitle);
        addCellRow(tblCliente, "Cliente / Razão:", cliente != null ? cliente.getNome() : servico.getClienteNome(), "Telefone:", servico.getTelefone(), fLabel, fVal);
        addCellRow(tblCliente, "CPF / CNPJ:", cliente != null && cliente.getCpfCnpj() != null ? cliente.getCpfCnpj() : "-", "Endereço:", cliente != null && cliente.getEndereco() != null ? cliente.getEndereco() : "-", fLabel, fVal);
        doc.add(tblCliente);
        doc.add(new Paragraph(" "));

        // 3. VEÍCULO E GARANTIA (Condicional)
        PdfPTable tblVeiculo = new PdfPTable(4);
        tblVeiculo.setWidthPercentage(100);
        tblVeiculo.setWidths(new float[]{20, 30, 20, 30});

        addHeaderCell(tblVeiculo, "OBJETO DO SERVIÇO / VEÍCULO", 4, fSecTitle);
        addCellRow(tblVeiculo, "Modelo do Veículo:", servico.getModelo(), "Placa do Carro:", servico.getPlaca(), fLabel, fVal);

        // Se mesesGarantia for maior que 0, exibe o tempo (3 meses ou 1 ano); senão, omite e deixa só "Garantia Nossa"
        if (servico.getMesesGarantia() > 0) {
            String tempoTexto = servico.getMesesGarantia() == 12 ? "1 ano" : servico.getMesesGarantia() + " meses";
            addCellRow(tblVeiculo, "Garantia:", "Garantia da Oficina (Nossa)", "Tempo de Garantia:", tempoTexto, fLabel, fVal);
        } else {
            // Apenas "Garantia da Oficina (Nossa)" ocupando a largura restante da linha
            PdfPCell clG = new PdfPCell(new Phrase("Garantia:", fLabel));
            clG.setPadding(4);
            PdfPCell cvG = new PdfPCell(new Phrase("Garantia da Oficina (Nossa)", fVal));
            cvG.setPadding(4);
            cvG.setColspan(3);
            tblVeiculo.addCell(clG);
            tblVeiculo.addCell(cvG);
        }

        doc.add(tblVeiculo);
        doc.add(new Paragraph(" "));

        // 4. DISCRIMINAÇÃO DOS SERVIÇOS E VALOR
        PdfPTable tblServicos = new PdfPTable(1);
        tblServicos.setWidthPercentage(100);

        addHeaderCell(tblServicos, "DISCRIMINAÇÃO DOS SERVIÇOS REALIZADOS E PEÇAS", 1, fSecTitle);
        PdfPCell cDesc = new PdfPCell(new Phrase(servico.getDescricao() != null && !servico.getDescricao().isBlank() ? servico.getDescricao() : "Serviço realizado conforme ordem.", fVal));
        cDesc.setPadding(10);
        cDesc.setMinimumHeight(80);
        tblServicos.addCell(cDesc);

        PdfPCell cTotal = new PdfPCell(new Phrase("VALOR TOTAL: " + servico.getValor(), fHeaderTitle));
        cTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cTotal.setPadding(8);
        tblServicos.addCell(cTotal);

        doc.add(tblServicos);
        doc.add(new Paragraph("\n"));

        // 5. CANHOTO DESTACÁVEL INFERIOR
        PdfPTable tblCanhoto = new PdfPTable(2);
        tblCanhoto.setWidthPercentage(100);
        tblCanhoto.setWidths(new float[]{70, 30});

        PdfPCell cCorte = new PdfPCell(new Phrase("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - CORTE AQUI - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", fSmall));
        cCorte.setBorder(Rectangle.NO_BORDER);
        cCorte.setColspan(2);
        cCorte.setHorizontalAlignment(Element.ALIGN_CENTER);
        tblCanhoto.addCell(cCorte);

        PdfPCell cCanhotoEsq = new PdfPCell();
        cCanhotoEsq.setBorder(Rectangle.BOX);
        cCanhotoEsq.setPadding(6);
        cCanhotoEsq.addElement(new Paragraph((emp.getNomeFantasia() != null ? emp.getNomeFantasia() : "") + "  |  Fone: " + (emp.getTelefone() != null ? emp.getTelefone() : ""), fLabel));
        cCanhotoEsq.addElement(new Paragraph("Comprovante de Entrada - O.S. Nº " + (servico.getId() != null ? String.format("%04d", servico.getId()) : "NOVA"), fSecTitle));
        cCanhotoEsq.addElement(new Paragraph("Cliente: " + (cliente != null ? cliente.getNome() : servico.getClienteNome()) + "  |  Veículo: " + servico.getVeiculo(), fVal));
        cCanhotoEsq.addElement(new Paragraph(emp.getMensagemRodape() != null ? emp.getMensagemRodape() : "Apresente este canhoto para retirar o veículo.", fSmall));
        tblCanhoto.addCell(cCanhotoEsq);

        PdfPCell cAssinatura = new PdfPCell();
        cAssinatura.setBorder(Rectangle.BOX);
        cAssinatura.setPadding(6);
        cAssinatura.addElement(new Paragraph("Data: " + servico.getData() + "\n\n________________________\nAssinatura do Cliente", fSmall));
        tblCanhoto.addCell(cAssinatura);

        doc.add(tblCanhoto);

        doc.close();

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(pdfFile);
        }

        return pdfFile;
    }

    private static void addHeaderCell(PdfPTable table, String text, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setBackgroundColor(new java.awt.Color(241, 245, 249));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addCellRow(PdfPTable table, String l1, String v1, String l2, String v2, Font fL, Font fV) {
        PdfPCell cl1 = new PdfPCell(new Phrase(l1, fL));
        cl1.setPadding(4);
        PdfPCell cv1 = new PdfPCell(new Phrase(v1, fV));
        cv1.setPadding(4);
        PdfPCell cl2 = new PdfPCell(new Phrase(l2, fL));
        cl2.setPadding(4);
        PdfPCell cv2 = new PdfPCell(new Phrase(v2, fV));
        cv2.setPadding(4);
        table.addCell(cl1); table.addCell(cv1); table.addCell(cl2); table.addCell(cv2);
    }
}