/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is
 * published under the terms of the CPAL v1.0 license, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.hdfs.automation.testcases;

import org.apache.hadoop.fs.FileStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.hdfs.automation.HDFSTestParent;
import org.mule.modules.hdfs.automation.RegressionTests;
import org.mule.modules.tests.ConnectorTestUtils;

import java.util.List;

import static org.junit.Assert.*;

@Category({RegressionTests.class})
public class SetOwnerTestCases extends HDFSTestParent {

    String filePath;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("setOwnerTestData");
        runFlowAndGetPayload("make-directories");
        filePath = getTestRunMessageValue("path");
    }

    @Test
    public void testSetOwner() {
        try {
            upsertOnTestRunMessage("path", getTestRunMessageValue("rootPath"));
            List<FileStatus> fileStatuses = runFlowAndGetPayload("list-status");
            String oldOwner = fileStatuses.get(0).getOwner();

            upsertOnTestRunMessage("path", filePath);
            runFlowAndGetPayload("set-owner");

            upsertOnTestRunMessage("path", getTestRunMessageValue("rootPath"));
            fileStatuses = runFlowAndGetPayload("list-status");
            String newOwner = fileStatuses.get(0).getOwner();

            assertNotSame(oldOwner, newOwner);
            assertEquals(getTestRunMessageValue("ownername"), newOwner);

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        runFlowAndGetPayload("delete-directory");
    }
}