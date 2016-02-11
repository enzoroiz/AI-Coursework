import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

/**
 * Class that uses Apache POI library to write and read one excel sheet
 * 
 * @author enzoroiz
 *
 */
public class WriteExcel {
	/**
	 * Instance fields
	 */
	private final String SHEET = "assets/Results.xls";
	private HSSFSheet sheet;
	private HSSFWorkbook workbook;

	public WriteExcel() {
		// Open the model localized in ASSETS
		try {
			InputStream in = new FileInputStream(new File(SHEET));
			workbook = new HSSFWorkbook(in);
			if (workbook == null) {
				throw new FileNotFoundException("File Not Found");
			}
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Write the data passed as an array in columns
	 * 
	 * @param data
	 *            to be written
	 * @param startingColumn
	 * @return the first empty column
	 */
	public int writeDataInColumns(ArrayList<SignalInfo> data,
			Integer startingColumn) {
		if (startingColumn == null) {
			startingColumn = 0;
		}

		int startingRow = 2;
		int j = 0;
		CellStyle style = createStyle();
		Row row;
		Cell cell;

		// For each row
		for (int i = 0; i < data.size(); i++) {
			// For each column
			for (j = 0; j < SignalInfo.NUM_INFO; j++) {
				row = sheet.getRow(i + startingRow);

				if (row == null) {
					row = sheet.createRow(i + startingRow);
				}

				cell = row.createCell(j + startingColumn);
				writeCell(cell, data.get(i).get(j));
				cell.setCellStyle(style);
			}
		}

		startingColumn += j;

		return startingColumn;
	}

	/**
	 * Write an cell given the object to write
	 * 
	 * @param cell
	 * @param obj
	 */
	private void writeCell(Cell cell, Object obj) {
		if (obj == null) {
			cell.setCellValue("");
		}

		if (obj instanceof Date)
			cell.setCellValue((Date) obj);
		else if (obj instanceof Boolean)
			cell.setCellValue((Boolean) obj);
		else if (obj instanceof String)
			cell.setCellValue((String) obj);
		else if (obj instanceof Double) {
			Double d = (Double) obj;
			if (d.isNaN()) {
				cell.setCellValue("-");
			} else {
				cell.setCellValue((Double) obj);
			}
		} else if (obj instanceof Integer)
			cell.setCellValue((Integer) obj);
		else if (obj instanceof Float) {
			cell.setCellValue((Float) obj);
		}

	}

	/**
	 * After putting the information in the Excel sheet, finally writes it
	 * 
	 * @throws IOException
	 */
	public void writeWorkbook() throws IOException {
		FileOutputStream outputStream = new FileOutputStream(new File(SHEET));
		workbook.write(outputStream);
		outputStream.close();
	}

	/**
	 * Create a style for an excel cell
	 * 
	 * @return
	 */
	private CellStyle createStyle() {
		CellStyle style = workbook.createCellStyle();

		// Fonte arial
		Font fontArial = workbook.createFont();
		fontArial.setFontName("Arial");
		style.setFont(fontArial);

		// Borders
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		// Vertical alignment (Center)
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		return style;
	}

	/**
	 * Return an certain cell given the
	 * 
	 * @param row
	 * @param col
	 * @return cell
	 */
	public Cell getCell(int row, int col) {
		HSSFCell cell = sheet.getRow(row).getCell(col);
		return cell;
	}
}
