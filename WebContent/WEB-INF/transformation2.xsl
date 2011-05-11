<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:java="http://xml.apache.org/xalan/java" 
	xmlns:javaHelper="photonCollector.TransformationHelper" 
	version="2.0" 
	exclude-result-prefixes="xsl xs java javaHelper pp">
	<xsl:template match="/">
		<logs>
			<xsl:apply-templates select="*"/>
		</logs>
	</xsl:template>
	<xsl:template match="photocontainer">
		<!-- Entscheinde Variable, ob Metadaten hochgeladen werden soll oder nicht -->
		<xsl:variable name="var_descision">
			<xsl:text>1</xsl:text>
		</xsl:variable>
		<xsl:variable name="var_uploaded">
			<xsl:text>false</xsl:text>
		</xsl:variable>
		
		
		<xsl:if test="count(log/error) &gt; 0">
			<xsl:variable name="var_descision">
				<xsl:text>0</xsl:text>
			</xsl:variable>
		</xsl:if>
		
		<log>
			<xsl:attribute name="picture">
				<xsl:value-of select="log/@picture"/>
			</xsl:attribute>
			<xsl:for-each select="log/error">
			<error><xsl:value-of select="." /></error>
			</xsl:for-each>
			<xsl:for-each select="log/warning">
			<warning><xsl:value-of select="." /></warning>
			</xsl:for-each>
			
			<xsl:if test="number($var_descision) = number('1')">
				<xsl:variable name="var_uploaded">
					<xsl:value-of select="boolean(javaHelper:uploadMetadata(number(@id), photo))"/>
				</xsl:variable>
			</xsl:if>
			<result>			
				<xsl:text>Der Upload des Bildes war </xsl:text>
				<xsl:if test="string($var_uploaded) = 'false'">
					<xsl:text>NICHT </xsl:text>
				</xsl:if>
				<xsl:text>erfolgreich!</xsl:text>
			</result>
		</log>
	</xsl:template>
</xsl:stylesheet>