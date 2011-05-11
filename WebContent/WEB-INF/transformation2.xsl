<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:java="http://xml.apache.org/xalan/java" 
	xmlns:javaHelper="photonCollector.TransformationHelper" 
	version="2.0" 
	exclude-result-prefixes="xsl xs java javaHelper">
	<xsl:template match="/">
		<log>
			<xsl:apply-templates select="*"/>
		</log>
	</xsl:template>
	<xsl:template match="photocontainer">
		<xsl:variable name="photoNode">
			<xsl:copy-of select="photo"></xsl:copy-of>
		</xsl:variable>
		<xsl:variable name="var_uploaded">
			<xsl:value-of select="boolean(javaHelper:push(number(@id), photo))"/>
		</xsl:variable>
		<xsl:value-of select="$var_uploaded"/>		
	</xsl:template>
</xsl:stylesheet>