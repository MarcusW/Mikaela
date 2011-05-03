<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xhtml xsl xs">

	<xsl:output method="xml" encoding="ISO-8859-1" indent="yes"/>
	
	<xsl:strip-space elements="*" />
	
	<xsl:template match="/">
		<pp>
			<xsl:apply-templates select="*"/>
		</pp>
	</xsl:template>

	<xsl:template match="xhtml:head">
	</xsl:template>
	
	<xsl:template match="xhtml:body">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	
	<xsl:template match="xhtml:h1">
	</xsl:template>

	<xsl:template match="xhtml:li">
		<xsl:variable name="var_name">
			<xsl:value-of select="./xhtml:h2"/>
		</xsl:variable>
		<!-- Da es kein else gibt nutze ich choose mit otherwise -->
		<xsl:choose>
			<xsl:when test="count(document('http://141.76.61.48:8103/photos')//pp:photo[@original_filename=$var_name]) &gt; 0">
				<!-- TODO: Muessen Daten evtl. aktualisiert werden? -->
			</xsl:when>
			<xsl:otherwise>
			<!-- TODO: Hochladen zu Webservice -->
			<photo>
				<xsl:attribute name="file_name"><xsl:value-of select="$var_name"/></xsl:attribute>
				<!-- Muss noch angepasst werden -->
				<xsl:attribute name="created"><xsl:value-of select="./xhtml:dl/xhtml:dd[6]"/></xsl:attribute>
				<!-- "Foto " muss raus -->
				<xsl:attribute name="title"><xsl:value-of select="./xhtml:img/@alt"/></xsl:attribute>
				<!-- Muss aus Bilddatei ausgelesen werden!!! -->
				<xsl:attribute name="geo_lat">?</xsl:attribute>
				<!-- Muss aus Bilddatei ausgelesen werden!!! -->
				<xsl:attribute name="geo_long">?</xsl:attribute>
				<xsl:attribute name="aperture"><xsl:value-of select="./xhtml:dl/xhtml:dd[3]"/></xsl:attribute>
				<xsl:attribute name="exposuretime"><xsl:value-of select="./xhtml:dl/xhtml:dd[4]"/></xsl:attribute>
				<xsl:attribute name="focallength"><xsl:value-of select="./xhtml:dl/xhtml:dd[5]"/></xsl:attribute>
				<xsl:attribute name="user_name"><xsl:value-of select="./xhtml:author"/></xsl:attribute>
				<!-- Was genau soll das? -->
				<xsl:attribute name="author">1</xsl:attribute>
				<xsl:attribute name="upload_complete">1</xsl:attribute>
				<tags>
					<xsl:for-each select="./xhtml:dl/xhtml:dd[7]/xhtml:ul/xhtml:li">
						<tag>
							<xsl:value-of select="."/>
						</tag>
					</xsl:for-each>
				</tags>
				<description>
				<xsl:value-of select="./xhtml:p"/>
				</description>
			</photo>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>