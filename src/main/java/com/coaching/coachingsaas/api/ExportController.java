package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.domain.Payment;
import com.coaching.coachingsaas.domain.Student;
import com.coaching.coachingsaas.repo.PaymentRepository;
import com.coaching.coachingsaas.repo.StudentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    public ExportController(StudentRepository studentRepository,
                            PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping("/students")
    public void exportStudents(Authentication auth, HttpServletResponse response) throws Exception {

        Long coachingId = coachingId(auth);

        List<Student> students =
                studentRepository.findAllByCoaching_IdOrderByIdDesc(coachingId);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student Name");
        header.createCell(1).setCellValue("Mobile");
        header.createCell(2).setCellValue("Active");

        int rowIdx = 1;

        for (Student s : students) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getName());
            row.createCell(1).setCellValue(s.getMobile());
            row.createCell(2).setCellValue(s.isActive() ? "Yes" : "No");
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=students.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/defaulters")
    public void exportDefaulters(Authentication auth, HttpServletResponse response) throws Exception {

        Long coachingId = coachingId(auth);
        String currentMonth = YearMonth.now().toString();

        List<Student> students =
                studentRepository.findAllByCoaching_IdAndActiveTrueOrderByIdDesc(coachingId);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Defaulters");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student Name");
        header.createCell(1).setCellValue("Mobile");
        header.createCell(2).setCellValue("Pending Amount");
        header.createCell(3).setCellValue("Overdue Months");

        int rowIdx = 1;

        for (Student s : students) {
            int monthlyDue = s.getBatches().stream().mapToInt(b -> b.getMonthlyFee()).sum();
            int paid = (int) paymentRepository.sumPaidForMonth(
                    coachingId,
                    s.getId(),
                    currentMonth
            );

            int pending = Math.max(0, monthlyDue - paid);

            if (pending > 0) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getName());
                row.createCell(1).setCellValue(s.getMobile());
                row.createCell(2).setCellValue(pending);
                row.createCell(3).setCellValue(1); // basic version
            }
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=defaulters.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/fees")
    public void exportFees(Authentication auth, HttpServletResponse response) throws Exception {

        Long coachingId = coachingId(auth);

        List<Payment> payments =
                paymentRepository.findAllByCoaching_IdOrderByPaidAtDesc(coachingId);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fees Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student");
        header.createCell(1).setCellValue("Amount");
        header.createCell(2).setCellValue("Month");
        header.createCell(3).setCellValue("Date");
        header.createCell(4).setCellValue("Note");

        int rowIdx = 1;

        for (Payment p : payments) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getStudent().getName());
            row.createCell(1).setCellValue(p.getAmount());
            row.createCell(2).setCellValue(p.getPaidForMonth());
            row.createCell(3).setCellValue(p.getPaidAt().toString());
            row.createCell(4).setCellValue(p.getNote() == null ? "" : p.getNote());
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=fees_report.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}