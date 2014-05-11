/*
 * Copyright (C) 2014 Matthew Green
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jwebnet.nerdreportcard.utils;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import net.jwebnet.nerdreportcard.ReportRecord;

/**
 *
 * @author Matthew Green
 */
public class SQLDatabase implements Database {

    public ReportRecord getReport(Integer reportId) {
        ReportRecord record = null;

        return record;
    }

    public List<ReportRecord> getReports(String username) {
        List<ReportRecord> reportList = new LinkedList<ReportRecord>();

        return reportList;
    }

    public void addReport(ReportRecord record) throws IOException {

    }

    public void editReport(ReportRecord record) throws IOException {

    }

    public void deleteReport(Integer reportId) throws IOException {

    }
}
