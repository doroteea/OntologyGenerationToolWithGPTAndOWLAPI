import {xml2json} from "xml-js";

export class Ontology {
  private classes: any[];
  private objectProperties: any[];
  private owlJson : any;
  constructor(owlText: string | ArrayBuffer) {
    if (typeof owlText === "string") {
      this.owlJson = JSON.parse(xml2json(owlText, {compact: true, spaces: 4}));
    }
    this.classes = this.parseClasses(this.owlJson);
    this.objectProperties = this.parseObjectProperties(this.owlJson);
  }

  parseClasses(owlJson: { [x: string]: { [x: string]: any; }; }) {
    const classes = [];
    if (owlJson && owlJson['rdf:RDF'] && owlJson['rdf:RDF']['owl:Class']) {
      const owlClasses = owlJson['rdf:RDF']['owl:Class'];
      if (Array.isArray(owlClasses)) {
        owlClasses.forEach(owlClass => {
          classes.push({
            id: owlClass['_attributes']['rdf:ID'],
            label: owlClass['rdfs:label'] && owlClass['rdfs:label']._text ? owlClass['rdfs:label']._text : owlClass['@_rdf:about']
          });
        });
      } else {
        classes.push({
          id: owlClasses['@_rdf:about'],
          label: owlClasses['rdfs:label'] ? owlClasses['rdfs:label']._text : owlClasses['@_rdf:about']
        });
      }
    }
    return classes;
  }

  parseObjectProperties(owlJson: { [x: string]: { [x: string]: any; }; }) {
    const objectProperties: any[] = [];
    if (owlJson && owlJson['rdf:RDF'] && owlJson['rdf:RDF']['owl:ObjectProperty']) {
      const owlObjectProperties = owlJson['rdf:RDF']['owl:ObjectProperty'];
      if (Array.isArray(owlObjectProperties)) {
        owlObjectProperties.forEach(owlObjectProperty => {
          console.log(owlObjectProperty);
          objectProperties.push({
            id: owlObjectProperty['rdfs:label']._text,
            label: owlObjectProperty['rdfs:label'] ? owlObjectProperty['rdfs:label']._text : owlObjectProperty['@_rdf:about'],
            source: {
              id: (owlObjectProperty['rdfs:domain']['_attributes']['rdf:resource']).substring(1),
            },
            target: {
              id: (owlObjectProperty['rdfs:range']['_attributes']['rdf:resource']).substring(1),
            }
          });
          console.log(objectProperties[0])
        });
      } else {
        objectProperties.push({
          id: owlObjectProperties['@_rdf:about'],
          label: owlObjectProperties['rdfs:label'] ? owlObjectProperties['rdfs:label']['#text'] : owlObjectProperties['@_rdf:about'],
          source: {
            id: owlObjectProperties['rdfs:domain']['@_rdf:resource']
          },
          target: {
            id: owlObjectProperties['rdfs:range']['@_rdf:resource']
          }
        });
      }
    }
    return objectProperties;
  }


  getClasses() {
    return this.classes;
  }

  getObjectProperties() {
    return this.objectProperties;
  }

}
