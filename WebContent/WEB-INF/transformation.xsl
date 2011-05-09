<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="2.0" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:java="http://xml.apache.org/xalan/java"
	xmlns:javaHelper="photonCollector.TransformationHelper"
	
	exclude-result-prefixes="xhtml xsl xs java javaHelper">

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

	<xsl:template match="xhtml:ul">
		<xsl:apply-templates select="*"/>
	</xsl:template>

	<xsl:template match="xhtml:li">
		<xsl:variable name="var_name">
			<xsl:value-of select="./xhtml:h2"/>
		</xsl:variable>
		
		<!-- Da es kein else gibt nutze ich choose mit otherwise -->
		<xsl:choose>
		<!-- TODO: kleiner in groesser gleich aendern -->
			<xsl:when test="count(document('http://141.76.61.48:8103/photos')//pp:photo[@original_filename=$var_name]) &lt; 0">
				<!-- TODO: Muessen Daten evtl. aktualisiert werden? -->
			</xsl:when>
			<xsl:otherwise>
			<!-- TODO: Hochladen zu Webservice -->
			<photo>
				<xsl:attribute name="file_name"><xsl:value-of select="$var_name"/></xsl:attribute>
				<!-- Muss noch angepasst werden -->
				<!-- string(./xhtml:dl/xhtml:dd[6]) -->
				<xsl:variable name="var_date">
					<xsl:value-of select="string(./xhtml:dl/xhtml:dd[6])"/>
				</xsl:variable>
				<xsl:variable name="var_url">
					<xsl:value-of select="./xhtml:img/@src"/>
				</xsl:variable>
				<xsl:attribute name="upload_complete"><xsl:value-of select="javaHelper:uploadImage($var_url)"/></xsl:attribute>
				<xsl:attribute name="created"><xsl:value-of select="javaHelper:dateToUnixTimestamp($var_date)"/></xsl:attribute>
				<!-- Fuer Title muss "Foto " aus dem alt-Attribut des img raus -->
				<xsl:attribute name="title"><xsl:value-of select="substring(./xhtml:img/@alt,6)"/></xsl:attribute>
				<!-- Muss aus Bilddatei ausgelesen werden!!! -->
				<xsl:attribute name="geo_lat"><xsl:value-of select="javaHelper:getMetaInformation('geo_lat', $var_url)"/></xsl:attribute>
				<!-- Muss aus Bilddatei ausgelesen werden!!! -->
				<xsl:attribute name="geo_long"><xsl:value-of select="javaHelper:getMetaInformation('geo_long', $var_url)"/></xsl:attribute>
				<xsl:attribute name="aperture"><xsl:value-of select="./xhtml:dl/xhtml:dd[3]"/></xsl:attribute>
				<xsl:attribute name="exposuretime"><xsl:value-of select="./xhtml:dl/xhtml:dd[4]"/></xsl:attribute>
				<xsl:attribute name="focallength"><xsl:value-of select="./xhtml:dl/xhtml:dd[5]"/></xsl:attribute>
				<xsl:attribute name="user_name"><xsl:value-of select="./xhtml:h3[2]"/></xsl:attribute>
				<!-- gueltige user ID gdw Autor ist User -->
				<xsl:variable name="var_author">
					<xsl:value-of select="./xhtml:h3[2]"/>
				</xsl:variable>
				<!-- ist Author des Bildes User im System? JA!-> UserID NEIN!-> -1 -->
				<xsl:attribute name="author">
					<xsl:if test="count(document('http://141.76.61.48:8103/users')//pp:user[@username=$var_author]) &gt; 0">
						<xsl:value-of select="document('http://141.76.61.48:8103/users')//pp:user[@username=$var_author]/@id"/>
					</xsl:if>
					<xsl:if test="count(document('http://141.76.61.48:8103/users')//pp:user[@username=$var_author]) = 0">
						<xsl:text>-1</xsl:text>
					</xsl:if>
				</xsl:attribute>
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
