# coding: utf-8

"""
    CrestDB REST API

    Crest Rest Api to manage data for calibration files.  # noqa: E501

    OpenAPI spec version: 2.0
    Contact: andrea.formica@cern.ch
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


import pprint
import re  # noqa: F401

import six

from crestapi.models.crest_base_response import CrestBaseResponse  # noqa: F401,E501
from crestapi.models.generic_map import GenericMap  # noqa: F401,E501
from crestapi.models.global_tag_map_dto import GlobalTagMapDto  # noqa: F401,E501


class GlobalTagMapSetDto(CrestBaseResponse):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """

    """
    Attributes:
      swagger_types (dict): The key is attribute name
                            and the value is attribute type.
      attribute_map (dict): The key is attribute name
                            and the value is json key in definition.
    """
    swagger_types = {
        'format': 'str',
        'resources': 'list[GlobalTagMapDto]'
    }

    attribute_map = {
        'format': 'format',
        'resources': 'resources'
    }

    def __init__(self, format='GlobalTagMapSetDto', resources=None):  # noqa: E501
        """GlobalTagMapSetDto - a model defined in Swagger"""  # noqa: E501

        self._format = None
        self._resources = None
        self.discriminator = None

        if format is not None:
            self.format = format
        if resources is not None:
            self.resources = resources

    @property
    def format(self):
        """Gets the format of this GlobalTagMapSetDto.  # noqa: E501


        :return: The format of this GlobalTagMapSetDto.  # noqa: E501
        :rtype: str
        """
        return self._format

    @format.setter
    def format(self, format):
        """Sets the format of this GlobalTagMapSetDto.


        :param format: The format of this GlobalTagMapSetDto.  # noqa: E501
        :type: str
        """

        self._format = format

    @property
    def resources(self):
        """Gets the resources of this GlobalTagMapSetDto.  # noqa: E501


        :return: The resources of this GlobalTagMapSetDto.  # noqa: E501
        :rtype: list[GlobalTagMapDto]
        """
        return self._resources

    @resources.setter
    def resources(self, resources):
        """Sets the resources of this GlobalTagMapSetDto.


        :param resources: The resources of this GlobalTagMapSetDto.  # noqa: E501
        :type: list[GlobalTagMapDto]
        """

        self._resources = resources

    def to_dict(self):
        """Returns the model properties as a dict"""
        result = {}

        for attr, _ in six.iteritems(self.swagger_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(map(
                    lambda x: x.to_dict() if hasattr(x, "to_dict") else x,
                    value
                ))
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(map(
                    lambda item: (item[0], item[1].to_dict())
                    if hasattr(item[1], "to_dict") else item,
                    value.items()
                ))
            else:
                result[attr] = value
        if issubclass(GlobalTagMapSetDto, dict):
            for key, value in self.items():
                result[key] = value

        return result

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.to_dict())

    def __repr__(self):
        """For `print` and `pprint`"""
        return self.to_str()

    def __eq__(self, other):
        """Returns true if both objects are equal"""
        if not isinstance(other, GlobalTagMapSetDto):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """Returns true if both objects are not equal"""
        return not self == other
