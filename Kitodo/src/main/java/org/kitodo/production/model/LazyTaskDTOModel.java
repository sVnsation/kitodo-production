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

package org.kitodo.production.model;

import static java.lang.Math.toIntExact;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.index.query.QueryShardException;
import org.kitodo.data.database.enums.TaskStatus;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.exceptions.FilterException;
import org.kitodo.production.services.data.FilterService;
import org.kitodo.production.services.data.TaskService;
import org.primefaces.PrimeFaces;
import org.primefaces.model.SortOrder;

public class LazyTaskDTOModel extends LazyDTOModel {

    private boolean onlyOwnTasks = false;
    private boolean showAutomaticTasks = false;
    private boolean hideCorrectionTasks = false;
    private List<TaskStatus> taskStatusRestriction = new LinkedList<>();

    /**
     * Creates a LazyTaskDTOModel instance that allows fetching data from the data
     * source lazily, e.g. only the number of datasets that will be displayed in the
     * frontend.
     *
     * @param taskService the TaskService which is used to retrieve data from the data
     *                      source
     */
    public LazyTaskDTOModel(TaskService taskService) {
        super(taskService);
        this.taskStatusRestriction.add(TaskStatus.OPEN);
        this.taskStatusRestriction.add(TaskStatus.INWORK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object>
            filters) {
        if (indexRunning()) {
            try {
                HashMap<String, String> filterMap = new HashMap<>();
                if (!StringUtils.isBlank(this.filterString)) {
                    filterMap.put(FilterService.FILTER_STRING, this.filterString);
                }
                setRowCount(toIntExact(((TaskService)searchService).countResults(filterMap, this.onlyOwnTasks,
                        this.hideCorrectionTasks, this.showAutomaticTasks, this.taskStatusRestriction)));
                entities = ((TaskService)searchService).loadData(first, pageSize, sortField, sortOrder, filterMap,
                        this.onlyOwnTasks, this.hideCorrectionTasks, this.showAutomaticTasks,
                        this.taskStatusRestriction);
                logger.trace("{} entities loaded!", entities.size());
                return entities;
            } catch (DataException | ElasticsearchStatusException | QueryShardException e) {
                setRowCount(0);
                logger.error(e.getMessage(), e);
            } catch (FilterException e) {
                setRowCount(0);
                PrimeFaces.current().executeScript("PF('sticky-notifications').renderMessage("
                        + "{'summary':'Filter error','detail':'" + e.getMessage() + "','severity':'error'});");
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.info("Index not found!");
        }
        return new LinkedList<>();
    }

    /**
     * Set onlyOwnTasks.
     *
     * @param onlyOwnTasks as boolean
     */
    public void setOnlyOwnTasks(boolean onlyOwnTasks) {
        this.onlyOwnTasks = onlyOwnTasks;
    }

    /**
     * Set taskStatusRestriction.
     *
     * @param taskStatusRestriction as org.kitodo.data.database.enums.TaskStatus
     */
    public void setTaskStatusRestriction(List<TaskStatus> taskStatusRestriction) {
        this.taskStatusRestriction = taskStatusRestriction;
    }

    /**
     * Set showAutomaticTasks.
     *
     * @param showAutomaticTasks as boolean
     */
    public void setShowAutomaticTasks(boolean showAutomaticTasks) {
        this.showAutomaticTasks = showAutomaticTasks;
    }

    /**
     * Get hideCorrectionTasks.
     *
     * @return value of hideCorrectionTasks
     */
    public boolean isHideCorrectionTasks() {
        return hideCorrectionTasks;
    }

    /**
     * Set hideCorrectionTasks.
     *
     * @param hideCorrectionTasks as boolean
     */
    public void setHideCorrectionTasks(boolean hideCorrectionTasks) {
        this.hideCorrectionTasks = hideCorrectionTasks;
    }

}
