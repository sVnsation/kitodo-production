/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.forms.createprocess;

import java.io.File;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.kitodo.api.MdSec;
import org.kitodo.api.MetadataEntry;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataeditor.rulesetmanagement.StructuralElementViewInterface;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.production.services.ServiceManager;
import org.primefaces.model.TreeNode;

public class ProcessDetailIT {

    @Test
    public void shouldCopyProcessDetail() throws Exception {
        RulesetManagementInterface ruleset = ServiceManager.getRulesetManagementService().getRulesetManagement();
        ruleset.load(new File("src/test/resources/rulesets/ruleset_test.xml"));
        StructuralElementViewInterface divisionView = ruleset.getStructuralElementView("Monograph", "edit",
            Locale.LanguageRange.parse("en"));
        LogicalDivision division = new LogicalDivision();
        division.setType("Monograph");
        MetadataEntry titleDocMain = new MetadataEntry();
        titleDocMain.setDomain(MdSec.SOURCE_MD);
        titleDocMain.setKey("TitleDocMain");
        titleDocMain.setValue("Lorem Ipsum");
        division.getMetadata().add(titleDocMain);
        ProcessFieldedMetadata processFieldedMetadata = new ProcessFieldedMetadata(division, divisionView);
        TreeNode treeNode = processFieldedMetadata.getTreeNode();
        ProcessDetail processDetail = (ProcessDetail) treeNode.getChildren().get(0).getData();
        int beforeCopying = treeNode.getChildCount();
        processDetail.copy();
        Assert.assertEquals("Should have copied metadata", beforeCopying + 1, treeNode.getChildCount());
    }
}
