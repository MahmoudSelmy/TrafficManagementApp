package com.masstudio.selmy.tmc.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.masstudio.selmy.tmc.POJO.TableElement;
import com.masstudio.selmy.tmc.POJO.TableRow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by tech lap on 12/04/2017.
 */

public class Excel {
    private DatabaseReference ref;
    private List<TableRow> data = new ArrayList<>();
    private ProgressDialog progress;
    private Context context;
    public Excel(Context context){
        ref = FirebaseDatabase.getInstance().getReference().child("STATS");;
        ref.keepSynced(true);
        progress = new ProgressDialog(context);
        this.context = context;
        Log.d("EXCEL","INIT");
    }
    private static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }
    private void creatSheet() {
        //Create blank workbook
        //XSSFWorkbook workbook = new XSSFWorkbook();
        Workbook workbook = new HSSFWorkbook();
        //Create a blank sheet
        Sheet spreadsheet = workbook.createSheet(" Employee Info ");
        //Create row object
        Row row;
        //This data needs to be written (Object[])
        Map< String, List<String> > empinfo =
                new TreeMap< String, List<String> >();
        List<TableElement> header = data.get(0).getElements();
        List<String> names = new ArrayList<>();
        Log.d("EXCEL","Header");
        names.add("Date");
        for (TableElement tableElement : header){
            names.add(tableElement.getName());
        }
        empinfo.put( ""+ 0 ,names);
        int j =1;
        for (TableRow element : data){
            List<String> values = new ArrayList<>();
            values.add(element.getDate());
            for (TableElement tableElement : element.getElements()){
                values.add(tableElement.getValue());
            }
            empinfo.put( ""+ (j++),values);
        }
        Log.d("EXCEL","LOOP_FINISH");
        //Iterate over data and write to sheet
        Set< String > keyid = empinfo.keySet();
        int rowid = 0;
        for (String key : keyid)
        {
            row = spreadsheet.createRow(rowid++);
            List<String> objectArr = empinfo.get(key);
            int cellid = 0;
            for (Object obj : objectArr)
            {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }
        }
        Log.d("EXCEL","LOOP_FINISH 1");
        //Write the workbook in file system
        FileOutputStream out = null;
        try {
            Log.d("EXCEL","TRY");
            String rootString = Environment.getExternalStorageDirectory().toString();
            File path2 = new File(rootString + "/TMC Excel Reports");
            path2.mkdir();
            /*
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            path.mkdir();
            */
            File file = new File(path2,"segments.xls");
            file.createNewFile();
            out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.d("EXCELSHEET","1");
            e.printStackTrace();
            Log.d("EXCEL","CATCH 1");
        } catch (IOException e) {
            Log.d("EXCELSHEET","2" + e.getMessage());
            e.printStackTrace();
            Log.d("EXCEL","CATCH 2");
        }
        cancelDialogue();
        // to go to file manager
        /*
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        context.startActivity(intent);
        */

    }
    public void getData(){
        showDialogue();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    TableRow element = child.getValue(TableRow.class);
                    data.add(element);
                }
                creatSheet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void showDialogue(){
        progress.setMessage("wait please ");
        progress.setCancelable(false);
        progress.show();
    }
    private void cancelDialogue(){
        progress.dismiss();
    }
}
