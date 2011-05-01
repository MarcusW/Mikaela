<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xhtml xsl xs">

	<xsl:output method="xml" encoding="ISO-8859-1" indent="no"/>
	<xsl:template match="/">
		<pp>
			<xsl:apply-templates match="*"/>
		</pp>
	</xsl:template>

	<xsl:template match="xhtml:head">
	</xsl:template>
	
	<xsl:template match="xhtml:body">
		<xsl:apply-templates match="*"/>
	</xsl:template>
	
	<xsl:template match="xhtml:h1">
	</xsl:template>

	<xsl:template match="xhtml:li">
		<xsl:variable name="var_name">
			<xsl:value-of select="./xhtml:h2"/>
		</xsl:variable>
		<xsl:value-of select="$var_name"/>
		
		<xsl:value-of select="$var_newID"/>
		
		<!-- Da es kein else gibt nutze ich choose mit otherwise -->
		<xsl:choose>
			<xsl:when test="count(document('http://141.76.61.48:8103/photos')//pp:photo[@original_filename=$var_name]) &gt; 0">
				<p>gefunden</p>
				<!-- TODO: Muessen Daten evtl. aktualisiert werden? -->
			</xsl:when>
			<xsl:otherwise>
				<p>nicht gefunden</p>
				<!-- TODO: Hochladen zu Webservice -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>