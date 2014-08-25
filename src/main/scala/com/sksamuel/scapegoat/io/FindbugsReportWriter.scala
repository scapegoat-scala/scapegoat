package com.sksamuel.scapegoat.io

import java.util.UUID

/** @author Stephen Samuel */
class FindbugsReportWriter {

  def xml(projectName: String) =

    <bugcollection analysistimestamp={ System.currentTimeMillis.toString } release=" " sequence="0" timestamp={ System.currentTimeMillis.toString } version="1.3.9">
      <project projectname={ projectName }>
      </project>
      <buginstance abbrev="CN" category="BAD_PRACTICE" instancehash={ UUID.randomUUID.toString } instanceoccurrencemax="0" instanceoccurrencenum="0" priority="2" type="CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE">
        <shortmessage>Class defines clone() but doesn't implement Cloneable</shortmessage>
        <longmessage>org.sprunck.bee.Bee defines clone() but doesn't implement Cloneable</longmessage>
        <class classname="org.sprunck.bee.Bee" primary="true">
          <sourceline classname="org.sprunck.bee.Bee" end="31" start="6">
            <message>At 'unknown':[lines 6-31]</message>
          </sourceline>
          <message>In class org.sprunck.bee.Bee</message>
        </class>
        <method classname="org.sprunck.bee.Bee" isstatic="false" name="clone" primary="true" signature="()Ljava/lang/Object;">
          <sourceline classname="org.sprunck.bee.Bee" end="31" endbytecode="25" start="31" startbytecode="0"></sourceline>
          <message>In method org.sprunck.bee.Bee.clone()</message>
        </method>
        <sourceline classname="org.sprunck.bee.Bee" end="31" endbytecode="25" start="31" startbytecode="0" synthetic="true">
          <message>At 'unknown' :[line 31]</message>
        </sourceline>
      </buginstance>
      <errors errors="0" missingclasses="0"></errors>
      <findbugssummary alloc_mbytes="0" clock_seconds="0" cpu_seconds="0" gc_seconds="0" num_packages="0" peak_mbytes="0" priority_1="1" priority_2="3" referenced_classes="0" timestamp="Fri, 23 Jul 2010 20:35:05 +0200" total_bugs="0" total_classes="0" total_size="0" vm_version="0">
        <filestats bugcount="3" bughash="df1120c1c4b7708412d471df8e18b310" path="org/sprunck/bee/" size="11"></filestats>
        <filestats bugcount="1" bughash="2838bfeb77300e43a82da09454a1d353" path="org/sprunck/foo/" size="12"></filestats>
        <filestats bugcount="0" path="org/sprunck/tests/Unknown" size="22"></filestats>
        <packagestats package="org.sprunck.bee" priority_1="1" priority_2="2" total_bugs="3" total_size="11" total_types="1">
          <classstats bugs="3" class="org.sprunck.bee.Bee" interface="false" priority_1="1" priority_2="2" size="11" sourcefile="Unknown"></classstats>
        </packagestats>
        <packagestats package="org.sprunck.foo" priority_2="1" total_bugs="1" total_size="12" total_types="1">
          <classstats bugs="1" class="org.sprunck.foo.Foo" interface="false" priority_2="1" size="12" sourcefile="Unknown"></classstats>
        </packagestats>
        <packagestats package="org.sprunck.tests" total_bugs="0" total_size="22" total_types="2">
          <classstats bugs="0" class="org.sprunck.tests.BeeTest" interface="false" size="9" sourcefile="Unknown"></classstats>
          <classstats bugs="0" class="org.sprunck.tests.FooTest" interface="false" size="13" sourcefile="Unknown"></classstats>
        </packagestats>
      </findbugssummary>
    </bugcollection>
}
