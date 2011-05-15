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
		<log>
			<!-- attribut picture mit dateiname um einzelne logs zuzuordnen  -->
			<xsl:attribute name="picture">
				<xsl:value-of select="log/@picture"/>
			</xsl:attribute>
			<!-- uebernahme der input log-eintraege -->
			<xsl:for-each select="log/error">
			<error><xsl:value-of select="." /></error>
			</xsl:for-each>
			<xsl:for-each select="log/warning">
			<warning><xsl:value-of select="." /></warning>
			</xsl:for-each>
			<!-- Upload der Metadaten fuer den fall, dass es eine ID gibt -->
 			<xsl:variable name="var_uploaded">
				<xsl:choose>
					<xsl:when test="string(@id)">
						<xsl:value-of select="boolean(javaHelper:uploadMetadata(number(@id), photo))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- abschliessender Log-Eintrag zeigt das Ergebnis des Vorgangs  -->
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