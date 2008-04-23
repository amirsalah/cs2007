<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="text"></xsl:output>
  
	<!--Global variable definition: newline-->
	<xsl:variable name="NEWLINE">
	   <xsl:text>
</xsl:text>
    </xsl:variable>
	<xsl:variable name="INDENT">
	    <xsl:value-of select="'    '"></xsl:value-of></xsl:variable>

	<xsl:template match="/">
      <xsl:apply-templates select="/FUNCDEF"></xsl:apply-templates>
      <xsl:apply-templates select="/FUNCDEF/INTERFACE/METHOD"></xsl:apply-templates></xsl:template>
	

	<!--Gen: struct definition, e.g. typedef struct FileInfo FileInfo--><xsl:template
	    match="/FUNCDEF"
	>
    <xsl:call-template name="genStructTypedef">
        <xsl:with-param name="structs" select="."></xsl:with-param></xsl:call-template>
</xsl:template><!--Gen: method-->
      <xsl:template match="/FUNCDEF/INTERFACE/METHOD">
      <xsl:call-template name="genMethodHead">
          <xsl:with-param name="method" select="."></xsl:with-param></xsl:call-template>
      <xsl:value-of select="concat($NEWLINE, '{', $NEWLINE)"></xsl:value-of>
      <xsl:call-template name="genPntr">
          <xsl:with-param name="method" select="."></xsl:with-param>
          <xsl:with-param name="paramPos" select="1"></xsl:with-param></xsl:call-template>
                  <xsl:value-of select="concat('    int badtype;', $NEWLINE)"></xsl:value-of>
                  <xsl:call-template name="genVariables">
                      <xsl:with-param name="method" select="."></xsl:with-param></xsl:call-template>
  </xsl:template>
    
    <!--Function: gen method head, e.g. static void b_readfile1(task *tsk, pntr *argstack)-->
    <xsl:template name="genMethodHead">
    <xsl:param name="method"></xsl:param>
        <xsl:variable name="f_method">
            <xsl:call-template name="needForced">
            <xsl:with-param name="method" select="$method"></xsl:with-param>
            <xsl:with-param name="paramPos" select="1"></xsl:with-param></xsl:call-template>
        </xsl:variable>
    <xsl:choose>
        <xsl:when test="$f_method='true'">
            <xsl:value-of select="concat('static void b_', $method/@name, '1(task *tsk, pntr *argstack)')"></xsl:value-of></xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="concat('static void b_', $method/@name, '(task *tsk, pntr *argstack)')"></xsl:value-of></xsl:otherwise></xsl:choose>
</xsl:template>

    <!--Function: generate pntr to argstack. e.g. pntr val0 = argstack[0]-->
  <xsl:template name="genPntr">
      <xsl:param name="method"></xsl:param>
      <xsl:param name="paramPos"></xsl:param>
      <xsl:variable name="numParam" select="count($method/PARAMETER)"></xsl:variable>
      
      <xsl:if test="$paramPos &lt;= $numParam"><xsl:value-of select="concat('    pntr val', $paramPos, ' = argstack[', $numParam - $paramPos, '];', $NEWLINE)"></xsl:value-of>
          <xsl:call-template name="genPntr">
              <xsl:with-param name="method" select="$method"></xsl:with-param>
              <xsl:with-param name="paramPos" select="$paramPos + 1"></xsl:with-param></xsl:call-template></xsl:if>
    </xsl:template>

    <!--Fucntion: recusively TEST if given method contains any its parameters contain String or struct, return xl:boolean-->
    <xsl:template name="needForced">
    <xsl:param name="method"></xsl:param>
    <xsl:param name="paramPos"></xsl:param>
    <xsl:if test="count($method/PARAMETER)&gt;0">
        <xsl:variable name="paramType" select="$method/PARAMETER[$paramPos]/@type"></xsl:variable><xsl:choose>
            <xsl:when test="$paramType='String' or starts-with($paramType, &quot;struct&quot;)">
                <xsl:value-of select="true()"></xsl:value-of></xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$paramPos=count($method/PARAMETER)">
                        <xsl:value-of select="false()"></xsl:value-of></xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="needForced">
                        <xsl:with-param name="method" select="$method"></xsl:with-param>
                        <xsl:with-param name="paramPos" select="$paramPos + 1"></xsl:with-param></xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose></xsl:if>
    </xsl:template>
    
    <!--Function: gen variable definition, e.g. char *fileName;-->
    <xsl:template name="genVariables" >
    <xsl:param name="method"></xsl:param>
    <xsl:for-each select="$method/PARAMETER">
        <xsl:choose>
            <xsl:when test="./@type='int'">
                <xsl:value-of select="concat($INDENT, 'int ', ./@name, ';') "></xsl:value-of></xsl:when>
            <xsl:when test="./@type='String'">
                <xsl:value-of select="concat($INDENT, 'char *', ./@name, ';') "></xsl:value-of></xsl:when>
            <xsl:when test='starts-with(./@type, "struct")'>
                <xsl:value-of select="concat($INDENT, substring-after(./@type, ' '), ' ', ./@name, ';') "></xsl:value-of></xsl:when>
            <xsl:when test="@type='double'">
                <xsl:value-of select="concat($INDENT, 'double ', ./@name, ';') "></xsl:value-of></xsl:when></xsl:choose>
        <xsl:value-of select="$NEWLINE"></xsl:value-of></xsl:for-each></xsl:template>
        
        <!--Function: gen struct typedef, e.g. typedef struct FileInfo FileInfo--><xsl:template
        name="genStructTypedef"
    >
    <xsl:param name="structs"></xsl:param>
    <xsl:for-each select="$structs/STRUCT">
        <xsl:value-of select="concat('typedef struct ', substring-after(@type, &quot; &quot;), ' ', substring-after(@type, &quot; &quot;), ';', $NEWLINE)"></xsl:value-of></xsl:for-each>
    <xsl:value-of select="$NEWLINE"></xsl:value-of>
    <xsl:for-each select="$structs/STRUCT">
        <xsl:call-template name="genCStruct">
            <xsl:with-param name="struct" select="."></xsl:with-param></xsl:call-template></xsl:for-each>
    <xsl:value-of select="$NEWLINE"></xsl:value-of>
</xsl:template>

    <!--Function: gen C struct, e.g. struct FileInfo{char *fileName, int lenth}-->
    <xsl:template name="genCStruct" >
    <xsl:param name="struct"></xsl:param>
        <xsl:value-of select="concat(normalize-space($struct/@type), ' {', $NEWLINE)"></xsl:value-of>
        <xsl:for-each select="$struct/*">
            <xsl:choose>
                <xsl:when test="@type='int'">
                    <xsl:value-of select="concat($INDENT, 'int ', @name, ';', $NEWLINE)"></xsl:value-of></xsl:when>
                <xsl:when test="@type='double'">
                    <xsl:value-of select="concat($INDENT, 'double ', @name, ';', $NEWLINE)"></xsl:value-of></xsl:when>
                <xsl:when test="@type='String'">
                    <xsl:value-of select="concat($INDENT, 'char *', @name, ';', $NEWLINE)"></xsl:value-of></xsl:when>
                <xsl:when test='starts-with(@type, "struct")'>
                    <xsl:value-of select="concat($INDENT, substring-after(@type, &quot; &quot;), ' *', @name, ';', $NEWLINE)"></xsl:value-of></xsl:when>
	       </xsl:choose>
       </xsl:for-each>
    <xsl:value-of select="concat('};', $NEWLINE, $NEWLINE)"></xsl:value-of>
</xsl:template>

</xsl:stylesheet>

