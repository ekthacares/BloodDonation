package com.example.ekthacare.services;

import com.example.ekthacare.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private final UserService userService;

    public ExcelService(UserService userService) {
        this.userService = userService;
    }

    public UserUploadResult parseExcelFile(MultipartFile file) throws IOException {
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
                        duplicateUsers.add(user); // Add to duplicate list
                    }
                }
            }
        }
        return new UserUploadResult(validUsers, duplicateUsers);
    }

    public void saveUsersFromFile(MultipartFile file) throws IOException {
        UserUploadResult result = parseExcelFile(file);
        userService.saveUsers(result.getValidUsers()); // Save only valid users
        // Handle duplicates if necessary
    }

    private boolean isUserValid(User user) {
        return user.getDonorname() != null && !user.getDonorname().isEmpty() &&
               user.getMobile() != null && !user.getMobile().isEmpty() &&
               user.getEmailid() != null && !user.getEmailid().isEmpty() &&
               user.getDateofbirth() != null && // Ensure date of birth is set
               user.getAge() != null && user.getAge() > 0 && // Check if age is set and positive
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
