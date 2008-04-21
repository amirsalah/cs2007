<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="text"></xsl:output> 
          <xsl:template match = "/" >
               <xsl:apply-templates select = "/FUNCDEF/STRUCT" ></xsl:apply-templates>
          <xsl:apply-templates select="/FUNCDEF/INTERFACE/METHOD/PARAMETER"></xsl:apply-templates></xsl:template>
          
          <!--Gen methods in INTERFACE--><xsl:template match='/FUNCDEF/INTERFACE/METHOD'>
    <xsl:if test="substring(.//PARAMETER[2]/@type, 1, 6)='struct' ">
        <xsl:value-of select="'PARA '"></xsl:value-of></xsl:if>
    <xsl:value-of select="' '"></xsl:value-of>
    <xsl:for-each select="./PARAMETER">
        <xsl:value-of select="''"></xsl:value-of></xsl:for-each>
</xsl:template>
    <xsl:template match='PARAMETER[@type="String"]'>
        <xsl:call-template name="forceMethod">
            <xsl:with-param name="method" select=".."></xsl:with-param></xsl:call-template>
        <xsl:text>
</xsl:text>

    </xsl:template>
			
          <!--ELC Gen: for each struct-->
          <xsl:template match="/FUNCDEF/STRUCT">
          <xsl:variable name="structType" select="./@type"></xsl:variable><!--ELC GEN: get items from the object-->
          <xsl:for-each select="./*"><xsl:call-template name="getStructCom">
          	<xsl:with-param name="structType" select="$structType"></xsl:with-param>
          	<xsl:with-param name="structCom" select="."></xsl:with-param>
          	<xsl:with-param name="NO" select="position()-1"></xsl:with-param></xsl:call-template>
          	<xsl:text>
</xsl:text>
          </xsl:for-each>
          
          <!--ELC GEN: mk**** to generate ELC routine for end users to construct C struct in ELC programs-->
          <xsl:value-of select="concat('mk', $structType, ' ')"></xsl:value-of>
              <xsl:for-each select="./*">
                  <xsl:value-of select="concat(./@name, ' ')"></xsl:value-of>
                  </xsl:for-each>
              <xsl:value-of select="'= '"></xsl:value-of>
              <xsl:call-template name="gen_mkStruct">
                  <xsl:with-param name="struct" select="."></xsl:with-param>
                  <xsl:with-param name="pos" select="1"></xsl:with-param></xsl:call-template>
              <xsl:text>
</xsl:text>

              <!--ELC GEN: Force evaluation of all struct in -->
              <xsl:value-of select="concat('force', $structType, ' obj = (forcelist (mk', $structType, ' ')"></xsl:value-of>

          <xsl:for-each select="./*">
	<xsl:call-template name="forceStructCom">
		<xsl:with-param name="structType" select="$structType"></xsl:with-param>
		<xsl:with-param name="structCom" select="."></xsl:with-param></xsl:call-template></xsl:for-each>
          	<xsl:value-of select="'))'"></xsl:value-of>
          	<xsl:text>

</xsl:text>
          </xsl:template>
          
    <!--Functions: Generate forced strings, e.g. (forcelist str) , --><xsl:template name="createForcelist">
          <xsl:param name="paramNode"></xsl:param>
          	<xsl:choose>
          		<xsl:when test="@type='String'">
          			<xsl:value-of select="concat('(forcelist ', ./@name, ') ')"></xsl:value-of>
          		</xsl:when>
          		<xsl:when test="starts-with(@type, 'struct')">
          		    <xsl:value-of select="concat('(force', substring-after(@type, ' '), ' ', @name, ') ')"></xsl:value-of></xsl:when>
          		<xsl:otherwise>
          			<xsl:value-of select="concat(./@name, ' ')"></xsl:value-of></xsl:otherwise></xsl:choose></xsl:template><!--Function: Force evaluation of each struct component, e.g. forceFileInfo obj = ....-->
	<xsl:template name="forceStructCom">
	   <xsl:param name="structType"></xsl:param>
	   <xsl:param name="structCom"></xsl:param>
	       <xsl:choose>
	           <xsl:when test="$structCom/@type='String'">
	               <xsl:value-of select="concat('(forcelist (', $structType, '_', $structCom/@name, ' obj)) ' )"></xsl:value-of></xsl:when>
	           <xsl:when test="local-name($structCom)='STRUCTDEC'">
	               <xsl:value-of select="concat('(force', $structCom/@type, ' (', $structType, '_', $structCom/@name, ' obj)) ' )"></xsl:value-of></xsl:when>
	           <xsl:otherwise>
	               <xsl:value-of select="concat('(', $structType, '_', $structCom/@name, ' obj) ' )"></xsl:value-of>
	           </xsl:otherwise>
            </xsl:choose>
    </xsl:template>

    <!--Function: Retrive components of each struct: e.g. FileInfo_fileLen obj = (item 1 obj)-->
    <xsl:template name="getStructCom">
        <xsl:param name="structType"></xsl:param>
        <xsl:param name="structCom"></xsl:param>
        <xsl:param name="NO"></xsl:param>
            <xsl:value-of select="concat($structType, '_', $structCom/@name, ' obj = (item ', $NO, ' obj)' )"></xsl:value-of>
    </xsl:template>
    
    <!--Function: recursively generate: e.g. (cons item1 (cons item2... )))-->
    <xsl:template name="gen_mkStruct">
        <xsl:param name="struct"></xsl:param>
        <xsl:param name="pos"></xsl:param>
            <xsl:choose>
                <xsl:when test="count($struct/*)=$pos">
                    <xsl:value-of select="concat('(cons ', $struct/*[$pos]/@name, ' nil)' ) "></xsl:value-of></xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('(cons ', $struct/*[$pos]/@name, ' ')"></xsl:value-of>
                    <xsl:call-template name="gen_mkStruct">
                        <xsl:with-param name="struct" select="$struct"></xsl:with-param>
                        <xsl:with-param name="pos" select="$pos + 1"></xsl:with-param></xsl:call-template>
                    <xsl:value-of select="')'"></xsl:value-of>
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <!--Function: force the given method. e.g. read_file fileName = read_file1 (forcelist filename)-->
    <xsl:template name="forceMethod" >
    <xsl:param name="method"></xsl:param>
        <xsl:value-of select="concat($method/@name, ' ')"></xsl:value-of>
        <!--Gen: original parameter list-->
        <xsl:for-each select="$method/PARAMETER">
            <xsl:value-of select="concat(@name, ' ')"></xsl:value-of>
        </xsl:for-each>
        <xsl:value-of select="concat('= (', $method/@name, '1 ')"></xsl:value-of>
        <!--Gen: new parameter list, with forced strings and structs-->
        <xsl:for-each select="$method/PARAMETER" >
            <xsl:call-template name="createForcelist">
            <xsl:with-param name="paramNode" select="."></xsl:with-param></xsl:call-template>
        </xsl:for-each>
        <xsl:value-of select="concat(')')"></xsl:value-of>
    </xsl:template>

</xsl:stylesheet>
