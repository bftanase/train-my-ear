package ro.btanase.chordlearning.mybatisgen.client;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import ro.btanase.chordlearning.mybatisgen.model.Settings;
import ro.btanase.chordlearning.mybatisgen.model.SettingsExample;

public interface SettingsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int countByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int deleteByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int insert(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int insertSelective(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    List<Settings> selectByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    Settings selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int updateByExampleSelective(@Param("record") Settings record, @Param("example") SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int updateByExample(@Param("record") Settings record, @Param("example") SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int updateByPrimaryKeySelective(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PUBLIC.SETTINGS
     *
     * @mbggenerated Fri Apr 06 14:07:40 EEST 2012
     */
    int updateByPrimaryKey(Settings record);
    
    Settings selectByKey(String key);
}