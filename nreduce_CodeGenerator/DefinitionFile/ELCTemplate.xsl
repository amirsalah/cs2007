<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="text"></xsl:output> 
          <xsl:template match = "/" >
               <xsl:apply-templates select = "/FUNCDEF/STRUCT" ></xsl:apply-templates>
          <xsl:apply-templates select="/FUNCDEF/INTERFACE/METHOD/PARAMETER"></xsl:apply-templates></xsl:template>
          
          <!--Generate ELC wrappers for the methods in INTERFACE-->
          <xsl:template match='PARAMETER[@type="String"]'>
          	<!--Generate methods' name and corressonding parameters-->
          	<xsl:value-of select="concat(../@name, ' ')"></xsl:value-of>
          	<!--Generate a list of parameters-->
          	<xsl:for-each select="../PARAMETER">
          		<xsl:value-of select="concat(./@name, ' ')"></xsl:value-of>
          	</xsl:for-each>
          	<xsl:text>= (</xsl:text><!--Method name (its parent), add 1 to form actual builtin function-->
          	<xsl:value-of select="concat(../@name, '1 ')"></xsl:value-of>
          	<!--Generate a new list of parameters with validated parameters-->
          	<xsl:for-each
          		select="../PARAMETER">
	<xsl:call-template name="createForcelist">
          		<xsl:with-param name="paramNode" select="."></xsl:with-param></xsl:call-template></xsl:for-each>
          	<xsl:text>) 
</xsl:text></xsl:template>
<!--Functions: Generate the string in the form of (forcelist str) or not, according to the given parameter node-->

          <xsl:template name="createForcelist">
          <xsl:param name="paramNode"></xsl:param>
          	<xsl:choose>
          		<xsl:when test="@type='String'">
          			<xsl:value-of select="concat('(forcelist ', ./@name, ') ')"></xsl:value-of>
          		</xsl:when>
          		<xsl:otherwise>
          			<xsl:value-of select="concat(./@name, ' ')"></xsl:value-of></xsl:otherwise></xsl:choose></xsl:template>
          			
          <!--Generate ELC wrappers for each C struct-->
          <xsl:template match="/FUNCDEF/STRUCT">
          <xsl:variable name="structName" select="./@name"></xsl:variable>
          <xsl:for-each select="./*"><xsl:call-template name="getStructCom">
          	<xsl:with-param name="structName" select="$structName"></xsl:with-param>
          	<xsl:with-param name="structCom" select="."></xsl:with-param>
          	<xsl:with-param name="NO" select="position()"></xsl:with-param></xsl:call-template>
          	<xsl:text>
</xsl:text></xsl:for-each>
          
          <xsl:value-of select="concat('force', $structName, ' obj = (forcelist (mk', $structName, ' ')"></xsl:value-of>

          <!--Generate ELC wrappers for force evaluating all c struct--><xsl:for-each select="./*">
	<xsl:call-template name="forceStructCom">
		<xsl:with-param name="structName" select="$structName"></xsl:with-param>
		<xsl:with-param name="structCom" select="."></xsl:with-param></xsl:call-template></xsl:for-each>
          	<xsl:value-of select="'))'"></xsl:value-of>
          	<xsl:text>

</xsl:text>
          </xsl:template>
          
          <!--Function: Force evaluation of each struct component-->
          <xsl:template name="forceStructCom">
          	<xsl:param name="structName"></xsl:param>
          	<xsl:param name="structCom"></xsl:param>
          	<xsl:choose>
          		<xsl:when test="$structCom/@type='String'">
          			<xsl:value-of select="concat('(forcelist (', $structName, '_', $structCom/@name, ' obj)) ' )"></xsl:value-of></xsl:when>
          		<xsl:when test="$structCom/@type='struct'">
          			<xsl:value-of select="concat('(force', $structCom/@name, ' (', $structName, '_', $structCom/@name, ' obj)) ' )"></xsl:value-of></xsl:when>
          		<xsl:otherwise>
          			<xsl:value-of select="concat('(', $structName, '_', $structCom/@name, ' obj) ' )"></xsl:value-of></xsl:otherwise>
          	</xsl:choose>
          </xsl:template><!--Function: Retrive components of each struct--><xsl:template
          	name="getStructCom">
	<xsl:param name="structName"></xsl:param>
	<xsl:param name="structCom"></xsl:param>
	<xsl:param name="NO"></xsl:param>
	<xsl:value-of select="concat($structName, '_', $structCom/@name, ' obj = (item ', $NO, ' obj)' )"></xsl:value-of>
</xsl:template>
</xsl:stylesheet>
