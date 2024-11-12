package com.example.ekthacare.services;

import com.example.ekthacare.entity.User;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ExcelService(UserService userService) {
        this.userService = userService;
    }

    // Method to parse both Excel and CSV files
    public UserUploadResult parseFile(MultipartFile file, String fileType) throws IOException {
        if ("csv".equalsIgnoreCase(fileType)) {
            return parseCsvFile(file);
        } else {
            return parseExcelFile(file);
        }
    }

    // Parse Excel file
    private UserUploadResult parseExcelFile(MultipartFile file) throws IOException {
        List<User> validUsers = new ArrayList<>();
        List<User> duplicateUsers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                User user = new User();

                user.setDonorname(getCellStringValue(currentRow.getCell(0)));
                String mobile = getCellStringValue(currentRow.getCell(1)).trim();
                user.setMobile(mobile.isEmpty() ? null : mobile);
                user.setEmailid(getCellStringValue(currentRow.getCell(2)));

                Cell dateCell = currentRow.getCell(3);
                if (dateCell != null && DateUtil.isCellDateFormatted(dateCell)) {
                    LocalDate date = dateCell.getLocalDateTimeCellValue() != null ? 
                                     dateCell.getLocalDateTimeCellValue().toLocalDate() : null;
                    user.setDateofbirth(date);
                } else {
                    user.setDateofbirth(null);
                }

                user.setBloodgroup(getCellStringValue(currentRow.getCell(4)));

                double ageValue = getCellNumericValue(currentRow.getCell(5));
                if (ageValue > 0) {
                    user.setAge((int) ageValue);
                } else {
                    user.setAge(null);
                }

                user.setGender(getCellStringValue(currentRow.getCell(6)));
                user.setAddress(getCellStringValue(currentRow.getCell(7)));
                user.setCity(getCellStringValue(currentRow.getCell(8)));
                user.setState(getCellStringValue(currentRow.getCell(9)));

                if (isUserValid(user)) {
                    if (userService.findByMobile(user.getMobile()) == null) {
                        validUsers.add(user);
                    } else {
                        duplicateUsers.add(user);
                    }
                }
            }
        }

        return new UserUploadResult(validUsers, duplicateUsers);
    }

    // Parse CSV file with date handling for "dd-MM-yyyy"
    private UserUploadResult parseCsvFile(MultipartFile file) throws IOException {
        List<User> validUsers = new ArrayList<>();
        List<User> duplicateUsers = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true;

            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header row
                }

                User user = new User();
                user.setDonorname(nextLine[0]);
                user.setMobile(nextLine[1]);
                user.setEmailid(nextLine[2]);

                // Parse Date of Birth with custom formatter
                try {
                    if (nextLine[3] != null && !nextLine[3].isEmpty()) {
                        user.setDateofbirth(LocalDate.parse(nextLine[3], DATE_FORMATTER));
                    }
                } catch (DateTimeParseException e) {
                    throw new IOException("Error parsing date: " + nextLine[3] + ". Expected format: dd-MM-yyyy", e);
                }

                user.setBloodgroup(nextLine[4]);

                // Parse Age
                try {
                    int age = Integer.parseInt(nextLine[5]);
                    user.setAge(age);
                } catch (NumberFormatException e) {
                    user.setAge(null);
                }

                user.setGender(nextLine[6]);
                user.setAddress(nextLine[7]);
                user.setCity(nextLine[8]);
                user.setState(nextLine[9]);

                if (isUserValid(user)) {
                    if (userService.findByMobile(user.getMobile()) == null) {
                        validUsers.add(user);
                    } else {
                        duplicateUsers.add(user);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error reading CSV file: " + e.getMessage(), e);
        }

        return new UserUploadResult(validUsers, duplicateUsers);
    }

    // Save users after file parsing
    public void saveUsersFromFile(MultipartFile file, String fileType) throws IOException {
        UserUploadResult result = parseFile(file, fileType);
        userService.saveUsers(result.getValidUsers());
    }

    private boolean isUserValid(User user) {
        return user.getDonorname() != null && !user.getDonorname().isEmpty() &&
               user.getMobile() != null && !user.getMobile().isEmpty() &&
               user.getEmailid() != null && !user.getEmailid().isEmpty() &&
               user.getDateofbirth() != null &&
               user.getAge() != null && user.getAge() > 0 &&
               user.getBloodgroup() != null && !user.getBloodgroup().isEmpty() &&
               user.getGender() != null && !user.getGender().isEmpty() &&
               user.getAddress() != null && !user.getAddress().isEmpty() &&
               user.getCity() != null && !user.getCity().isEmpty() &&
               user.getState() != null && !user.getState().isEmpty();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private double getCellNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return 0;
        }
        return cell.getNumericCellValue();
    }
}
